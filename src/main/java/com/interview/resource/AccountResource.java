package com.interview.resource;

import com.interview.dao.AccountDAO;
import com.interview.entity.Account;
import com.interview.pojos.TransferRequest;
import com.interview.pojos.TransferResponse;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Account resource class exposing rest endpoints at /api/ URI.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);
    private AccountDAO dao;

    public AccountResource(AccountDAO dao) {
        this.dao = dao;
    }

    /**
     * Service to get all accounts` information.
     *
     * @return List of all accounts.
     */
    @UnitOfWork
    @GET
    @Path("/accounts")
    public List<Account> getAllAccounts() {
        LOGGER.info("Request: /accounts :: Getting all accounts");
        return dao.getAllAccounts();
    }

    /**
     * Get account by account_id.
     *
     * @param accountId
     * @return Account
     */
    @UnitOfWork
    @GET
    @Path("/accounts/{id}")
    public Account getAllAccounts(@PathParam("id") String accountId) {
        LOGGER.info("Request: /accounts/{id} :: Getting the account with id: " + accountId);
        return dao.getAccount(accountId);
    }

    /**
     * Post REST service to transfer balance from one account to other.
     *
     * @param transferRequest
     * @return
     * @throws Exception
     * @UnitOfWork is not transactional. Transaction is handled in DAO transfer balance method for sending customized
     * error response to the consumer of the service.
     * TransferResponse object is set as an Entity in the Response class.
     */
    @UnitOfWork(transactional = false)
    @POST
    @Path("/balance/transfer")
    public Response transferBalance(TransferRequest transferRequest) throws Exception {
        LOGGER.info("Request: /balance/transfer :: " + transferRequest.toString());
        TransferResponse trRes = new TransferResponse();
        try {
            dao.transferBalance(transferRequest);
            trRes.setErrorResponse(false);
            trRes.setMessage("Balance Transfer: Transaction Successful");
            return Response.status(Response.Status.OK).entity(trRes).type(MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            trRes.setErrorResponse(true);
            trRes.setMessage(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(trRes).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
