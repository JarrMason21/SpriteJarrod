/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.ws.rs.core.Application;

/**
 *
 * @author Jarrm
 */
/*
This class adds the the SpriteFacadeREST to the application resourse. This class 
also defines the JEE applications application path.
*/

/*
In order to authenticate a user, the appliaction needs to make a connection to 
the Appuser table in the database so that the users input can be compared 
against all the elements in the database to authenticate a user. The passwords
are hashes and so the hashing algorithm needs to be provided.
*/
@DatabaseIdentityStoreDefinition(
   dataSourceLookup = "${'java:comp/DefaultDataSource'}",
   callerQuery = "#{'select password from app.appuser where userid = ?'}",
   groupsQuery = "select groupname from app.appuser where userid = ?",
   hashAlgorithm = PasswordHash.class,
   priority = 10
)

/*
Authentication is to be done against the database identity store
and in order to do so, we need to setup the form logic here. And the page
that contains the form in this case is the index.xhtml.
*/
@FormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage = "/index.xhtml",
        errorPage = "/index.xhtml"))

/*Optionally we can use the BasicAuthenticationMechanismDefinition annotation
so we dont need to provide our own form. Note: in order for the RESTful api
to properly allow authentication, the BasicAuthenticationMechanismDefinition
annotation must be switched with the DatabaseIdentityStoreDefinition annotation.
*/
//@BasicAuthenticationMechanismDefinition
@Named
@ApplicationScoped
@javax.ws.rs.ApplicationPath("webresources")

public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(service.SpriteFacadeREST.class);
    }
    
}
