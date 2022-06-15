package org.acme.hibernate.orm.panache.service;

import io.smallrye.mutiny.Uni;
import org.acme.hibernate.orm.panache.model.Customer;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/customers")
@RegisterRestClient
public interface CustomerService {

    @GET
    @Path("{id}")
    Uni<Customer> getById(@PathParam("id") Long id);
}