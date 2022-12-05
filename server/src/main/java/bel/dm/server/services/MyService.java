package bel.dm.server.services;

import bel.dm.server.entities.Account;
import bel.dm.server.repos.AccountRepo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import share.interfaces.AccountService;
import share.interfaces.Statistics;

import java.util.concurrent.*;


@Service
@AllArgsConstructor
public class MyService implements AccountService, Statistics {

    private final AccountRepo accountRepository;
    private final Logger LOG = LoggerFactory.getLogger(MyService.class);
    /** sync object */
    private final Object lock = new Object();

    private Integer rRequestAmount;
    private Integer wRequestAmount;

    private Double rVelocity;
    private Double wVelocity;

    private Double oldRDoneAmount;
    private Double newRDoneAmount;
    private Double oldWDoneAmount;
    private Double newWDoneAmount;


    @Override
    public Long getAmount(Integer id) throws InterruptedException, ExecutionException, BadCredentialsException {
        rRequestAmount++;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        Future<Long> future = executorService.submit(() -> {
            Account account = accountRepository.findById(id).orElseThrow(() -> new BadCredentialsException("Account with id %s does not exist".formatted(id)));
            synchronized (lock) {
                lock.notify();
            }
            return account.getBalance();
        });
        executorService.schedule(() -> {
            future.cancel(true);
            executorService.shutdownNow();
            LOG.error("Canceling task due to long respond.");
            lock.notify();
        }, 5L, TimeUnit.SECONDS);
        while (!future.isDone()) {
            synchronized (lock) {
                lock.wait();
            }
        }
        executorService.shutdownNow();
        newRDoneAmount++;
        return future.get();
    }

    @Override
    public void addAmount(Integer id, Long value) throws InterruptedException, BadCredentialsException {
        wRequestAmount++;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        Future<?> future = executorService.submit(() -> {
            Account account = accountRepository.findById(id).orElseThrow(() -> new BadCredentialsException("Account with id %s does not exist".formatted(id)));
            account.setBalance(account.getBalance() + value);
            try {
                accountRepository.save(account);
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                LOG.error("Database connection interrupted. %s".formatted(e.getMessage()));
            }
        });
        executorService.schedule(() -> {
            future.cancel(true);
            executorService.shutdownNow();
            LOG.error("Canceling task due to long respond.");
            lock.notify();
        }, 5L, TimeUnit.SECONDS);
        while (!future.isDone()) {
            synchronized (lock) {
                lock.wait();
            }
        }
        executorService.shutdownNow();
        newWDoneAmount++;
    }

    @Override
    public void reset() {
        this.wVelocity = 0.0;
        this.rVelocity = 0.0;
        this.wRequestAmount = 0;
        this.rRequestAmount = 0;
        this.newRDoneAmount = 0.0;
        this.oldRDoneAmount = 0.0;
        this.newWDoneAmount = 0.0;
        this.oldWDoneAmount = 0.0;
    }

    @Override
    public Double getRVelocity() {
        return this.rVelocity;
    }

    @Override
    public Integer getRRequestAmount() {
        return this.rRequestAmount;
    }

    @Override
    public Double getWVelocity() {
        return this.wVelocity;
    }

    @Override
    public Integer getWRequestAmount() {
        return this.wRequestAmount;
    }

    /** rCount velocity counter. Fires each second*/
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void calculateRVelocity() {
        this.rVelocity = (this.oldRDoneAmount + this.newRDoneAmount) / 2;
        this.oldRDoneAmount = newRDoneAmount;
        this.newRDoneAmount = 0.0;
    }

    /** wCount velocity counter. Fires each second*/
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void calculateWVelocity() {
        this.wVelocity = (this.oldWDoneAmount + this.newWDoneAmount) / 2;
        this.oldWDoneAmount = this.newWDoneAmount;
        this.newWDoneAmount = 0.0;
    }
}
