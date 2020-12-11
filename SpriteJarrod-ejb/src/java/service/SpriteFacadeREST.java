/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import cst8218.maso0173.entity.Sprite;
import cst8218.maso0173.entity.AbstractFacade;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Jarrm
 */
/*
This class is responsible for handling the enterprise applications RESTful 
requests. All of the supported requests that this application can handle are in 
this class including GET, PUT, and POST requests. The methods also provide error
handling and the requests return responses.
*/
@Stateless
/*
In order to protect the RESTful api, the application will make sure that the
user either has Admin or REST permission. The roles are defined here with this 
annotaion. If a user is not a part of one of these two groups, they will not
gain access.
*/
@DeclareRoles("{Admin, RESTgroup}")
@RolesAllowed({"Admin", "RESTgroup"})
@Path("cst8218.maso0173.entity.sprite")
public class SpriteFacadeREST extends AbstractFacade<Sprite> {

    @PersistenceContext(unitName = "SpriteJarrod-ejbPU")
    private EntityManager em;

    public SpriteFacadeREST() {
        super(Sprite.class);
    }
    
    /*
    Root resource POST request will create a new entity if no id is provided in the
    entity body, or update an entity if the body id is not null and exists within the 
    database. It will be an invalid request if the entity body id is not null and
    does not exist within the database.
    */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response rootPostResponse(Sprite entity) {
        
        String message;
  
        if(entity.getId() == null){
            super.create(entity);
            message = "Entity Created";
            return Response.status(Response.Status.OK).entity(message).build();
        }
        
        Sprite oldSprite = super.find(entity.getId());
        
        if(entity.getId() != null && oldSprite != null){
            entity.update(oldSprite);
            super.edit(oldSprite);
            message = "Entity Updated";
            return Response.status(Response.Status.OK).entity(message).build();
        }       
        message = "Invalid Request";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }
    
    /*
    POST request that requires an entity id as its path that will update the 
    incoming entity with the old entity(not replace it). It will be an invalid 
    request if the id of the path does not match the id of the entity body or if
    an entity does not exist at the given path.
    */
    @POST
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postResponse(@PathParam("id") Long id, Sprite entity) {
        
        String message;
        Sprite oldSprite = super.find(entity.getId());
        
        if(id.equals(entity.getId()) && oldSprite != null){
            entity.update(oldSprite);
            super.edit(oldSprite);
            message = "Entity Updated";
            return Response.status(Response.Status.OK).entity(message).build();
        }
        message = "Invalid Request";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }

    /*
    PUT request that requires an entity id as its path that will replace the 
    incoming entity with the old entity(not update it). It will be an invalid 
    request if the id of the path does not match the id of the entity body or if
    an entity does not exist at the given path.
    */
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putResponse(@PathParam("id") Long id, Sprite entity) {
        
        String message;
        
        if(id.equals(entity.getId()) && super.find(entity.getId()) != null)
            if(entity.verifyData() == true){
                super.edit(entity);
                message = "Entity Replaced";
                return Response.status(Response.Status.OK).entity(message).build(); 
            }
        
        message = "Invalid Request";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }
    
    /*
    GET request the returns a specific entity that is determined by the id 
    provided in the URL path. Not necessary for the assignment but very useful.
    */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Sprite find(@PathParam("id") Long id) {
        return super.find(id);
    }
    
    /*
    GET request that requires the word "count" as its path that will return 
    the number of entities in the database.
    */
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return "Number of sprites in the database: " + String.valueOf(super.count());
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
/********Auto Generated Endpoints not required in Assignment1*******************
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sprite> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sprite> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
*******************************************************************************/ 
}
