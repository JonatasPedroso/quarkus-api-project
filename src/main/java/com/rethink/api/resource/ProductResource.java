package com.rethink.api.resource;

import com.rethink.api.entity.Product;
import com.rethink.api.service.ProductService;
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

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Gerenciamento de produtos")
public class ProductResource {
    
    @Inject
    ProductService productService;
    
    @GET
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Product.class)))
    public List<Product> list() {
        return productService.listAll();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
    @APIResponse(responseCode = "200", description = "Produto encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    public Product getById(@Parameter(description = "ID do produto", required = true) @PathParam("id") Long id) {
        return productService.findById(id);
    }
    
    @GET
    @Path("/search")
    @Operation(summary = "Pesquisar produtos por nome", description = "Busca produtos que contenham o termo no nome")
    @APIResponse(responseCode = "200", description = "Lista de produtos encontrados")
    public List<Product> search(@Parameter(description = "Termo de busca") @QueryParam("name") String name) {
        return productService.searchByName(name);
    }
    
    @GET
    @Path("/available")
    @Operation(summary = "Listar produtos disponíveis", description = "Retorna apenas produtos com quantidade maior que zero")
    @APIResponse(responseCode = "200", description = "Lista de produtos disponíveis")
    public List<Product> listAvailable() {
        return productService.listAvailable();
    }
    
    @POST
    @Operation(summary = "Criar novo produto", description = "Cadastra um novo produto no sistema")
    @APIResponse(responseCode = "201", description = "Produto criado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response create(@Valid Product product) {
        Product created = productService.create(product);
        return Response.created(URI.create("/api/products/" + created.id)).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @APIResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Product update(@Parameter(description = "ID do produto") @PathParam("id") Long id, 
                         @Valid Product product) {
        return productService.update(id, product);
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Excluir produto", description = "Remove um produto do sistema")
    @APIResponse(responseCode = "204", description = "Produto excluído com sucesso")
    @APIResponse(responseCode = "404", description = "Produto não encontrado")
    public Response delete(@Parameter(description = "ID do produto") @PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("/count")
    @Operation(summary = "Contar produtos", description = "Retorna o total de produtos cadastrados")
    @APIResponse(responseCode = "200", description = "Contagem retornada com sucesso")
    public Response count() {
        return Response.ok(new CountResponse(productService.countProducts(), 
                productService.countAvailableProducts())).build();
    }
    
    public static class CountResponse {
        public long total;
        public long available;
        
        public CountResponse(long total, long available) {
            this.total = total;
            this.available = available;
        }
    }
}