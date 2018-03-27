package com.interview.dao;

import com.interview.entity.Account;
import com.interview.exception.BalanceTransferException;
import com.interview.pojos.TransferRequest;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/*
DAO class to operate on Account entity.
 */
public class AccountDAO extends AbstractDAO<Account> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDAO.class);

    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Account> getAllAccounts() {
        return list(namedQuery("com.interview.dao.Account.getAllAccounts"));
    }

    public Account getAccount(String accountId) {
        Query query = namedQuery("com.interview.dao.Account.getAccount").setParameter("accId", accountId);
        if (query.list().size() == 0) {
            return null;
        } else {
            return (Account) query.list().get(0);
        }
    }

    /**
     * Selecting the balance from account and updating both sender and receiver accounts are
     * kept in a single transaction to make the operation atomic. The isolation level of the database
     * as well as hibernate is set to SERIALIZABLE to avoid the dirty, non-repeatable and phantom reads
     * in case of multiple processes trying to access the same accounts.
     * <p>
     * On exception the method rollbacks the transaction.
     * <p>
     * Reference: https://www.postgresql.org/docs/9.1/static/transaction-iso.html
     *
     * @param transferRequest
     * @throws Exception
     */
    public void transferBalance(TransferRequest transferRequest) throws Exception {
        if (!transferRequest.getReceiverAccountId().equals(transferRequest.getSenderAccountId())) {
            Session session = currentSession();
            Transaction trx = session.beginTransaction();
            trx.begin();
            try {
                if (transferRequest.getBalanceToTransfer() < 0) {
                    throw new BalanceTransferException("Can not transfer negative balance");
                }

                Account sender = getAccount(transferRequest.getSenderAccountId());
                Account receiver = getAccount(transferRequest.getReceiverAccountId());

                LOGGER.debug("Sender Account :" + sender);
                LOGGER.debug("Receiver Account :" + receiver);

                if (receiver != null && sender != null) {
                    if (sender.getBalance() >= transferRequest.getBalanceToTransfer()) {
                        sender.setBalance(sender.getBalance() - transferRequest.getBalanceToTransfer());
                        receiver.setBalance(receiver.getBalance() + transferRequest.getBalanceToTransfer());
                        session.update(sender);
                        session.update(receiver);
                        trx.commit();
                    } else {
                        LOGGER.error("Not enough balance in sender`s account");
                        throw new BalanceTransferException("Not enough balance in sender`s account");
                    }
                } else {
                    LOGGER.error("Sender or receiver does not exist");
                    throw new BalanceTransferException("Sender or receiver does not exist");
                }
            } catch (BalanceTransferException ex) {
                LOGGER.error("Catching Exception: " + ex.getMessage());
                LOGGER.info("Caught exception. Initiating transaction rollback.");
                trx.rollback();
                throw ex;
            } catch (Exception ex) {
                LOGGER.info("Caught exception. Initiating transaction rollback.");
                trx.rollback();
                throw ex;
            }
        }
    }
}
