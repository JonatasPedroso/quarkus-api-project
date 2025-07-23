package com.rethink.api.resource;

import com.rethink.api.entity.Customer;
import com.rethink.api.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customers", description = "Gerenciamento de clientes")
public class CustomerResource {
    
    @Inject
    CustomerService customerService;
    
    @GET
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public List<Customer> list() {
        return customerService.listAll();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo seu ID")
    @APIResponse(responseCode = "200", description = "Cliente encontrado")
    @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    public Customer getById(@Parameter(description = "ID do cliente") @PathParam("id") Long id) {
        return customerService.findById(id);
    }
    
    @GET
    @Path("/email/{email}")
    @Operation(summary = "Buscar cliente por email", description = "Retorna um cliente específico pelo seu email")
    @APIResponse(responseCode = "200", description = "Cliente encontrado")
    @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    public Customer getByEmail(@Parameter(description = "Email do cliente") @PathParam("email") String email) {
        return customerService.findByEmail(email);
    }
    
    @GET
    @Path("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Retorna um cliente específico pelo seu CPF")
    @APIResponse(responseCode = "200", description = "Cliente encontrado")
    @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    public Customer getByCpf(@Parameter(description = "CPF do cliente") @PathParam("cpf") String cpf) {
        return customerService.findByCpf(cpf);
    }
    
    @GET
    @Path("/search")
    @Operation(summary = "Pesquisar clientes por nome", description = "Busca clientes que contenham o termo no nome")
    @APIResponse(responseCode = "200", description = "Lista de clientes encontrados")
    public List<Customer> search(@Parameter(description = "Termo de busca") @QueryParam("name") String name) {
        return customerService.searchByName(name);
    }
    
    @GET
    @Path("/city/{city}")
    @Operation(summary = "Listar clientes por cidade", description = "Retorna clientes de uma cidade específica")
    @APIResponse(responseCode = "200", description = "Lista de clientes da cidade")
    public List<Customer> getByCity(@Parameter(description = "Nome da cidade") @PathParam("city") String city) {
        return customerService.findByCity(city);
    }
    
    @GET
    @Path("/state/{state}")
    @Operation(summary = "Listar clientes por estado", description = "Retorna clientes de um estado específico")
    @APIResponse(responseCode = "200", description = "Lista de clientes do estado")
    public List<Customer> getByState(@Parameter(description = "Sigla do estado") @PathParam("state") String state) {
        return customerService.findByState(state);
    }
    
    @GET
    @Path("/recent")
    @Operation(summary = "Listar clientes recentes", description = "Retorna os clientes mais recentemente cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de clientes recentes")
    public List<Customer> getRecent(
            @Parameter(description = "Número de clientes a retornar") 
            @QueryParam("limit") @DefaultValue("10") int limit) {
        return customerService.findRecentCustomers(limit);
    }
    
    @POST
    @Operation(summary = "Criar novo cliente", description = "Cadastra um novo cliente no sistema")
    @APIResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    public Response create(@Valid Customer customer) {
        Customer created = customerService.create(customer);
        return Response.created(URI.create("/api/customers/" + created.id)).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @APIResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    public Customer update(@Parameter(description = "ID do cliente") @PathParam("id") Long id, 
                          @Valid Customer customer) {
        return customerService.update(id, customer);
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente do sistema")
    @APIResponse(responseCode = "204", description = "Cliente excluído com sucesso")
    @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    @APIResponse(responseCode = "400", description = "Cliente possui pedidos vinculados")
    public Response delete(@Parameter(description = "ID do cliente") @PathParam("id") Long id) {
        customerService.delete(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("/count")
    @Operation(summary = "Contar clientes", description = "Retorna o total de clientes cadastrados")
    @APIResponse(responseCode = "200", description = "Contagem retornada com sucesso")
    public Response count() {
        return Response.ok(new CountResponse(customerService.countCustomers())).build();
    }
    
    public static class CountResponse {
        public long total;
        
        public CountResponse(long total) {
            this.total = total;
        }
    }
}