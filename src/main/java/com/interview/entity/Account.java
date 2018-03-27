package com.interview.entity;

import javax.persistence.*;
/*
Account entity mapped to accounts table.
 */

@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(name = "com.interview.dao.Account.getAllAccounts", query = "select a from Account a"),
        @NamedQuery(name = "com.interview.dao.Account.getAccount", query = "select a from Account a where a.accountId = :accId")
        //@NamedQuery(name = "com.interview.dao.Account.deductBalance", query = "update a set a.balance = (select balance - :deduction from Account a where a.accountId = :accId) where as.accountId = :accId ")
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "balance")
    private Double balance;

    public Account() {

    }

    public Account(String accountId, Double balance) {

        this.accountId = accountId;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (accountId != null ? !accountId.equals(account.accountId) : account.accountId != null) return false;
        return balance != null ? balance.equals(account.balance) : account.balance == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        return result;
    }
}
