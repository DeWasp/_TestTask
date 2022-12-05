package bel.dm.client.classes;



import share.interfaces.AccountService;
import share.interfaces.Statistics;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MyAccountService {

    private AccountService accountService;
    private Statistics statistics;

    public MyAccountService() {
        try {
            final String BINDING_NAME = "rmi.testtask";
            final Registry registry = LocateRegistry.getRegistry(5335);
            this.accountService = (AccountService) registry.lookup(BINDING_NAME);
            this.statistics = (Statistics) registry.lookup(BINDING_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAmount(Integer id, Long value) {
        try {
            accountService.addAmount(id, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getAmount(Integer id) {
        try {
            return accountService.getAmount(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public void reset() {
        try {
            statistics.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Double getRVelocity(){
        try {
            return statistics.getRVelocity();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public Integer getRRequestAmount(){
        try {
            return statistics.getRRequestAmount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Double getWVelocity(){
        try {
            return statistics.getWVelocity();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public Integer getWRequestAmount(){
        try {
            return statistics.getWRequestAmount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
