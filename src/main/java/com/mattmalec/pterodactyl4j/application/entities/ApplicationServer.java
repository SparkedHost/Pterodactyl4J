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

package com.mattmalec.pterodactyl4j.application.entities;

import com.mattmalec.pterodactyl4j.ServerStatus;
import com.mattmalec.pterodactyl4j.application.managers.ServerController;
import com.mattmalec.pterodactyl4j.application.managers.ServerManager;
import com.mattmalec.pterodactyl4j.entities.Server;
import com.mattmalec.pterodactyl4j.utils.Relationed;

import java.util.List;
import java.util.Optional;

/**
 * Represents a Pterodactyl {@link com.mattmalec.pterodactyl4j.application.entities.ApplicationServer ApplicationServer}.
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
	 */
	boolean isSuspended();

	/**
	 * The current status of the {@link com.mattmalec.pterodactyl4j.application.entities.ApplicationServer ApplicationServer}
	 * <br>If there's no status (server is in a normal state), then this method will return {@link com.mattmalec.pterodactyl4j.ServerStatus#UNKNOWN UNKNOWN}
	 *
	 * @return The current server status. Can return {@link com.mattmalec.pterodactyl4j.ServerStatus#UNKNOWN}
	 */
	ServerStatus getStatus();

	/**
	 * The external id of the ApplicationServer
	 *
	 * @return Possibly-null String containing the Servers's identifier.
	 */
	String getExternalId();

	/**
	 * The owner of the ApplicationServer
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.ApplicationUser ApplicationUser}
	 */
	Relationed<ApplicationUser> getOwner();

	/**
	 * The Node the ApplicationServer is running on
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.Node Node}
	 */
	Relationed<Node> getNode();

	/**
	 * The Allocations assigned to the ApplicationServer
	 *
	 * @return {@link java.util.Optional Optional} - Type {@link java.util.List List} of {@link com.mattmalec.pterodactyl4j.application.entities.Allocation Allocations}
	 */
	Optional<List<Allocation>> getAllocations();

	/**
	 * The main allocation for the ApplicationServer
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.Allocation Allocation}
	 */
	Relationed<Allocation> getDefaultAllocation();

	/**
	 * The Nest the ApplicationServer is using
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.Nest Nest}
	 */
	Relationed<Nest> getNest();

	/**
	 * The Egg the ApplicationServer is using
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.ApplicationEgg ApplicationEgg}
	 */
	Relationed<ApplicationEgg> getEgg();

	/**
	 * The Egg the ApplicationServer is using
	 *
	 * @return {@link com.mattmalec.pterodactyl4j.utils.Relationed Relationed} - Type {@link com.mattmalec.pterodactyl4j.application.entities.ApplicationEgg ApplicationEgg}
	 */
	Container getContainer();

	/**
	 * Returns the {@link com.mattmalec.pterodactyl4j.application.managers.ServerManager} for this ApplicationServer, used to modify
	 * the limits and feature of the server.
	 * <br>Managing a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The manager for this server
	 */
	ServerManager getManager();

	/**
	 * Returns the {@link com.mattmalec.pterodactyl4j.application.managers.ServerController} for this ApplicationServer, used to control
	 * the server state
	 * <br>Controlling a server requires an <b>Application API key</b> with the <b>Servers</b> permission with <b>Read &amp; Write</b> access.
	 *
	 * @return The controller for this server
	 */
	ServerController getController();

}
