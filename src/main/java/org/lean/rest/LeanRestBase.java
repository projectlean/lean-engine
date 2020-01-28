package org.lean.rest;


import org.lean.core.metastore.IHasIdentity;
import org.lean.core.metastore.LeanMetaStore;
import org.lean.core.metastore.MetaStoreFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.metastore.api.exceptions.MetaStoreException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

public class LeanRestBase<T extends IHasIdentity> {

  @JsonIgnore
  private Class<T> clazz;


  @JsonIgnore
  private String elementName;

  public LeanRestBase( Class<T> clazz, String elementName ) {
    this.clazz = clazz;
    this.elementName = elementName;
  }

  /**
   * List all elements of a T in JSON format
   *
   * @return
   */
  @GET
  @Produces( MediaType.APPLICATION_JSON )
  @SuppressWarnings( "unchecked" )
  @JsonIgnore
  public List<String> getAll() {
    try {
      MetaStoreFactory<T> factory = (MetaStoreFactory<T>) LeanMetaStore.getFactory( clazz );
      List<String> names = factory.getElementNames();
      Collections.sort(names);
      return names;
    } catch ( MetaStoreException e ) {
      throw new WebApplicationException( e, 500 ); // General error
    }
  }

  /**
   * Get a specific element with a given name
   *
   * @param name
   * @return The same element
   */
  @GET
  @Produces( MediaType.APPLICATION_JSON )
  @SuppressWarnings( "unchecked" )
  @JsonIgnore
  @Path("{name}")
  public T getByName( @PathParam( "name" ) String name ) {
    try {
      MetaStoreFactory<T> factory = (MetaStoreFactory<T>) LeanMetaStore.getFactory( clazz );

      T t = factory.loadElement( name );
      if ( t == null ) {
        // Not found
        throw new WebApplicationException( elementName + " with name '" + name + "' could not be found", 404 );
      }
      return t;
    } catch ( MetaStoreException e ) {
      System.out.println("Error loading from metastore: " + Const.getStackTracker(e));
      throw new WebApplicationException( e, 500 ); // General error
    }
  }

  /**
   * Add a new element T
   *
   * @param t
   * @return
   */
  @POST
  @Consumes( MediaType.APPLICATION_JSON )
  @Produces( MediaType.APPLICATION_JSON )
  @JsonIgnore
  @SuppressWarnings( "unchecked" )
  public T addNewElement( T t ) {
    try {
      MetaStoreFactory<T> factory = (MetaStoreFactory<T>) LeanMetaStore.getFactory( clazz );
      factory.saveElement( t );
      return t;
    } catch ( MetaStoreException e ) {
      throw new WebApplicationException( 500 ); // General error
    }
  }

  /**
   * Delete an element T with a given name
   *
   * @param name
   * @return
   */
  @DELETE
  @Path( "{name}" )
  @Produces( MediaType.APPLICATION_JSON )
  @JsonIgnore
  public T deleteElement( @PathParam( "name" ) String name ) {
    try {
      @SuppressWarnings( "unchecked" )
      MetaStoreFactory<T> factory = (MetaStoreFactory<T>) LeanMetaStore.getFactory( clazz );

      // We need the name of the element to delete
      //
      if ( StringUtils.isEmpty( name ) ) {
        throw new WebApplicationException( "Specify the element to delete", 500 );
      }

      T t = factory.loadElement( name );
      if ( t == null ) {
        // Not found
        throw new WebApplicationException( elementName + " with name '" + name + "' could not be found to delete", 404 );
      }

      factory.deleteElement( name );

      return t;
    } catch ( MetaStoreException e ) {
      throw new WebApplicationException( e, 500 ); // General error
    }
  }

  /**
   * Update an element T by name
   *
   * @param t The element to update
   * @return
   */
  @PUT
  @Consumes( MediaType.APPLICATION_JSON )
  @Produces( MediaType.APPLICATION_JSON )
  @JsonIgnore
  @SuppressWarnings( "unchecked" )
  @Path("/{name}")
  public T updateElement( @PathParam( "name" ) String name , T t ) {
    try {
      MetaStoreFactory<T> factory = (MetaStoreFactory<T>) LeanMetaStore.getFactory( clazz );
      T verify = factory.loadElement( name );
      if (verify==null) {
        throw new WebApplicationException( "Element with name '" + name + "' could not be found to update", 404 ); // Not found
      }
      // TODO: check if name change is possible and so on...
      factory.saveElement( t );
      return t;
    } catch ( MetaStoreException e ) {
      throw new WebApplicationException( 500 ); // General error
    }
  }
}
