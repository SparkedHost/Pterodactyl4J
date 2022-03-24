/*
 *    Copyright 2021 Matt Malec, and the Pterodactyl4J contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sparkedhost.pterodactyl4j.application.entities;

import com.sparkedhost.pterodactyl4j.PteroAction;
import com.sparkedhost.pterodactyl4j.ServerStatus;
import com.sparkedhost.pterodactyl4j.application.managers.*;
import com.sparkedhost.pterodactyl4j.entities.Server;
import com.sparkedhost.pterodactyl4j.utils.Relationed;
import com.sparkedhost.pterodactyl4j.application.managers.*;
import com.sparkedhost.pterodactyl4j.exceptions.LoginException;
import com.sparkedhost.pterodactyl4j.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Represents a Pterodactyl {@link ApplicationServer ApplicationServer}.
 * This should contain all information provided from the Pterodactyl instance about an ApplicationServer.
 *
 * @see PteroApplication#retrieveServers()
 * @see PteroApplication#retrieveServerById(long)
 * @see PteroApplication#retrieveServersByName(String, boolean)
 * @see PteroApplication#retrieveServersByOwner(ApplicationUser)
 * @see PteroApplication#retrieveServersByNode(Node)
 * @see PteroApplication#retrieveServersByLocation(Location)
 */
public interface ApplicationServer extends Server, ISnowflake {

	/**
	 * Returns whether or not this ApplicationServer is currently in a suspended state
	 * <p> The server suspension state can be controlled using {@link ServerController#suspend()} and {@link ServerController#unsuspend()}.
	 *
	 * @return True - if this ApplicationServer is suspended
	 *
	 * @deprecated This key will be removed in the coming Pterodactyl updates. Use {@link ApplicationServer#getStatus()} instead
	 */
	@Deprecated
	boolean isSuspended();

	/**
	 * The current status of the {@link ApplicationServer ApplicationServer}
	 * <br>If there's no status (server is in a normal state), then this method will return {@link ServerStatus#UNKNOWN UNKNOWN}
	 *
	 * @return The current server status. Can return {@link ServerStatus#UNKNOWN}
	 */
	ServerStatus getStatus();

	/**
	 * The external id of the ApplicationServer
	 *
	 * @return Possibly-null String containing the Servers's external id.
	 */
	String getExternalId();

	/**
	 * The owner of the ApplicationServer
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link ApplicationUser ApplicationUser}
	 */
	PteroAction<ApplicationUser> retrieveOwner();

	/**
	 * The {@link ApplicationUser owner} id of the ApplicationServer
	 *
	 * @return Long containing the owner id
	 *
	 * @see ApplicationServer#retrieveOwner()
	 */
	long getOwnerIdLong();

	/**
	 * The {@link ApplicationUser owner} id of the ApplicationServer
	 *
	 * @return Never-null String containing the owner id
	 *
	 * @see ApplicationServer#retrieveOwner()
	 */
	default String getOwnerId() {
		return Long.toUnsignedString(getOwnerIdLong());
	}

	/**
	 * The Node the ApplicationServer is running on
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link Node Node}
	 */
	PteroAction<Node> retrieveNode();

	/**
	 * The id of the {@link Node Node} the ApplicationServer is running on
	 *
	 * @return Long containing the node id
	 *
	 * @see ApplicationServer#retrieveNode()
	 */
	long getNodeIdLong();

	/**
	 * The id of the {@link Node Node} the ApplicationServer is running on
	 *
	 * @return Never-null String containing the node id
	 *
	 * @see ApplicationServer#retrieveNode()
	 */
	default String getNodeId() {
		return Long.toUnsignedString(getNodeIdLong());
	}

	/**
	 * The Allocations assigned to the ApplicationServer
	 *
	 * @return {@link java.util.Optional Optional} - Type {@link java.util.List List} of {@link ApplicationAllocation Allocations}
	 */
	Optional<List<ApplicationAllocation>> getAllocations();

	/**
	 * The main allocation for the ApplicationServer
	 *
	 * @return {@link Relationed Relationed} - Type {@link ApplicationAllocation Allocation}
	 */
	PteroAction<ApplicationAllocation> retrieveDefaultAllocation();

	/**
	 * The id of the main {@link ApplicationAllocation Allocation} for the ApplicationServer
	 *
	 * @return Long containing the main allocation id
	 *
	 * @see ApplicationServer#retrieveDefaultAllocation()
	 */
	long getDefaultAllocationIdLong();

	/**
	 * The id of the main {@link ApplicationAllocation Allocation} for the ApplicationServer
	 *
	 * @return Never-null String containing the main allocation id
	 *
	 * @see ApplicationServer#retrieveDefaultAllocation()
	 */
	default String getDefaultAllocationId() {
		return Long.toUnsignedString(getDefaultAllocationIdLong());
	}

	/**
	 * The Nest the ApplicationServer is using
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link Nest Nest}
	 */
	PteroAction<Nest> retrieveNest();

	/**
	 * The id of the {@link Nest Nest} for the ApplicationServer
	 *
	 * @return Long containing the nest id
	 *
	 * @see ApplicationServer#retrieveNest()
	 */
	long getNestIdLong();

	/**
	 * The id of the {@link Nest Nest} for the ApplicationServer
	 *
	 * @return Never-null String containing the nest id
	 *
	 * @see ApplicationServer#retrieveNest()
	 */
	default String getNestId() {
		return Long.toUnsignedString(getNestIdLong());
	}

	/**
	 * The Egg the ApplicationServer is using
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link ApplicationEgg ApplicationEgg}
	 */
	PteroAction<ApplicationEgg> retrieveEgg();

	/**
	 * The id of the {@link ApplicationEgg ApplicationEgg} for the ApplicationServer
	 *
	 * @return Long containing the egg id
	 *
	 * @see ApplicationServer#retrieveEgg()
	 */
	long getEggIdLong();

	/**
	 * The id of the {@link ApplicationEgg ApplicationEgg} for the ApplicationServer
	 *
	 * @return Never-null String containing the egg id
	 *
	 * @see ApplicationServer#retrieveNest()
	 */
	default String getEggId() {
		return Long.toUnsignedString(getEggIdLong());
	}

	/**
	 * The container of the ApplicationServer used to get startup objects
	 *
	 * @return The server container
	 *
	 * @see ApplicationServer#getStartupManager()
	 */
	Container getContainer();

	/**
	 * Returns the {@link ServerManager} for this ApplicationServer, used to modify
	 * the limits and feature of the server.
	 * <br>Managing a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The manager for this server
	 *
	 * @deprecated This will be removed in the next major release (non-beta). Use {@link ApplicationServer#getDetailManager()},
	 * {@link ApplicationServer#getBuildManager()}, or {@link ApplicationServer#getStartupManager()} instead
	 */
	@Deprecated
	ServerManager getManager();

	/**
	 * Returns the {@link ServerDetailManager ServerDetailManager} for this
	 * ApplicationServer, used to modify the server details
	 * <br>Managing a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The detail manager for this server
	 */
	ServerDetailManager getDetailManager();

	/**
	 * Returns the {@link ServerBuildManager ServerBuildManager} for this
	 * ApplicationServer, used to modify the build configuration limits and feature limits
	 * <br>Managing a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The build manager for this server
	 */
	ServerBuildManager getBuildManager();

	/**
	 * Returns the {@link ServerStartupManager ServerStartupManager} for this
	 * ApplicationServer, used to modify the startup parameters
	 * <br>Managing a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The build manager for this server
	 */
	ServerStartupManager getStartupManager();

	/**
	 * Returns the {@link ServerController} for this ApplicationServer, used to control
	 * the server state
	 * <br>Controlling a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The controller for this server
	 */
	ServerController getController();

	/**
	 * The Databases belonging to the ApplicationServer
	 * <br>This requires an <b>Application API key</b> with the <b>Databases</b> permission with <b>Read</b> access.
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link java.util.List List} of {@link ApplicationDatabase ApplicationDatabases}
	 */
	PteroAction<List<ApplicationDatabase>> retrieveDatabases();

	/**
	 * Retrieves an individual ApplicationDatabase represented by the provided id from Pterodactyl instance
	 * <br>This requires an <b>Application API key</b> with the <b>Databases</b> permission with <b>Read</b> access.
	 *
	 * @param  id
	 *         The id
	 *
	 * @throws LoginException
	 *         If the API key is incorrect or doesn't have the required permissions
	 *
	 * @throws NotFoundException
	 * 		   If the database cannot be found
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link ApplicationDatabase ApplicationDatabase}
	 */
	PteroAction<ApplicationDatabase> retrieveDatabaseById(String id);

	/**
	 * Retrieves an individual ApplicationDatabase represented by the provided id from Pterodactyl instance
	 * <br>This requires an <b>Application API key</b> with the <b>Databases</b> permission with <b>Read</b> access.
	 *
	 * @param  id
	 *         The id
	 *
	 * @throws LoginException
	 *         If the API key is incorrect or doesn't have the required permissions
	 *
	 * @throws NotFoundException
	 * 		   If the database cannot be found
	 *
	 * @return {@link PteroAction PteroAction} - Type {@link ApplicationDatabase ApplicationDatabase}
	 */
	default PteroAction<ApplicationDatabase> retrieveDatabaseById(long id) {
		return retrieveDatabaseById(Long.toUnsignedString(id));
	}

	/**
	 * Returns the {@link ApplicationDatabaseManager ApplicationDatabaseManager},
	 * used to create, reset passwords, and delete {@link ApplicationDatabase ApplicationDatabases}
	 * from the provided {@link ApplicationServer ApplicationServer}
	 * <p>Executing any of the containing methods requires an <b>Application API key</b> with the <b>Databases</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The Database Manager
	 */
	ApplicationDatabaseManager getDatabaseManager();

}
