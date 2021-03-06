package profesorp.restexample.resource;

import java.net.URI;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import profesorp.restexample.controller.LocaleController;
import profesorp.restexample.entity.Locales;

@Path("locale")
@Stateless
public class LocaleResource {
    
    @Inject   
    private  LocaleController localeController;
        
    private  Logger logger=  Logger.getLogger(LocaleResource.class.getName());
    
    @GET    
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllLocales() {        
        logger.log(Level.INFO, "Buscando todas las locales");
        return Response.ok(localeController.findAll()).build();
    }
  
    @GET
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLocale(@PathParam("codigo") String locCodi) {   
        Locales l= localeController.findById(locCodi);

        try {
            l.getNombre();
        } catch (javax.persistence.EntityNotFoundException k)
        {            
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(l).build();
      
    }
    
    @GET
    @Path("/{codigo}/{nombre}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLocale(@PathParam("codigo") String codigo, @PathParam("nombre") String nombre)
    {   
        Collection <Locales> l= localeController.findLike(codigo,nombre);
        if (l.size()==0)        
                return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(l).build();      
    }
    /**
     * Añadir Locale     
     * @param locale
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Locales locale) {

        if ( localeController.exists(locale.getCodigo())) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        localeController.create(locale);
        URI location = UriBuilder.fromResource(LocaleResource.class)
                .path("/{locale}")
                .resolveTemplate("locale", locale.getCodigo())
                .build();        
        return Response.created(location).build();
    }
    @PUT
    @Path("/{codigo}")
    public Response update(@PathParam("codigo") String codigo, Locales locales) {
         logger.log(Level.INFO, "Actualizando Locale: "+codigo+" de locale "+locales.getCodigo());
        if (!Objects.equals(codigo, locales.getCodigo())) {
            throw new BadRequestException("Propiedad 'codigo' de Objeto Locale debe coincidir con el parámetro mandado.");
        }
        localeController.update(locales);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{codigo}")
    public Response delete(@PathParam("codigo") String codigo) {
        logger.log(Level.INFO, "Borrando Locale: "+codigo);
        if ( ! localeController.exists(codigo)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        localeController.delete(codigo);
        return Response.ok().build();
    }


}
