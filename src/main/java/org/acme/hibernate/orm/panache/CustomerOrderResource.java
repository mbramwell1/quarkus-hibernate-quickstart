package org.acme.hibernate.orm.panache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import org.acme.hibernate.orm.panache.model.Customer;
import org.acme.hibernate.orm.panache.model.CustomerOrder;
import org.acme.hibernate.orm.panache.service.CustomerService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Path("orders")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CustomerOrderResource {

    @Inject
    @RestClient
    private CustomerService customerService;
    private static final Logger LOGGER = Logger.getLogger(CustomerOrderResource.class.getName());

    @GET
    public Uni<List<Customer>> get() {
        return CustomerOrder.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Uni<Customer> getSingle(Long id) {
        return CustomerOrder.findById(id);
    }

    @POST
    public Uni<Response> create(CustomerOrder order) {
        if (order == null || order.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        return customerService.getById(order.customerId).map(customer -> {
            order.customerName = customer.name;
            return order;
        }).onItem()
                .transformToUni(item -> Panache.withTransaction(order::persist)
                        .replaceWith(Response.ok(order).status(CREATED)::build));
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, CustomerOrder order) {
        if (order == null || order.item == null) {
            throw new WebApplicationException("order item was not set on request.", 422);
        }

        return Panache
                .withTransaction(() -> CustomerOrder.<CustomerOrder> findById(id)
                    .onItem().ifNotNull().invoke(entity -> entity.item = order.item)
                )
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return Panache.withTransaction(() -> CustomerOrder.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    /**
     * Create a HTTP response from an exception.
     *
     * Response Example:
     *
     * <pre>
     * HTTP/1.1 422 Unprocessable Entity
     * Content-Length: 111
     * Content-Type: application/json
     *
     * {
     *     "code": 422,
     *     "error": "order name was not set on request.",
     *     "exceptionType": "javax.ws.rs.WebApplicationException"
     * }
     * </pre>
     */
    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            Throwable throwable = exception;

            int code = 500;
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            // This is a Mutiny exception and it happens, for example, when we try to insert a new
            // order but the name is already in the database
            if (throwable instanceof CompositeException) {
                throwable = ((CompositeException) throwable).getCause();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", throwable.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", throwable.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}
