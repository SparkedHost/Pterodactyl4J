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

package com.sparkedhost.pterodactyl4j.client.entities;

import com.sparkedhost.pterodactyl4j.PowerAction;
import com.sparkedhost.pterodactyl4j.PteroAction;
import com.sparkedhost.pterodactyl4j.exceptions.LoginException;
import com.sparkedhost.pterodactyl4j.exceptions.NotFoundException;

import java.util.List;

public interface PteroClient {

    /**
     * Retrieves the Pterodactyl user account belonging to the API key
     *
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link Account Account}
     */
    PteroAction<Account> retrieveAccount();

    /**
     * Sets the power of a {@link ClientServer ClientServer}
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link Void}
     *
     * @deprecated Use {@link ClientServer#setPower(PowerAction)} instead
     */
    @Deprecated
    PteroAction<Void> setPower(ClientServer server, PowerAction powerAction);

    /**
     * Sends a command to a {@link ClientServer ClientServer}
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link Void}
     *
     * @deprecated Use {@link ClientServer#sendCommand(String)} instead
     */
    @Deprecated
    PteroAction<Void> sendCommand(ClientServer server, String command);

    /**
     * Retrieves the utilization of a {@link ClientServer ClientServer}
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link Utilization Utilization}
     *
     * @deprecated Use {@link ClientServer#retrieveUtilization()} instead
     */
    @Deprecated
    PteroAction<Utilization> retrieveUtilization(ClientServer server);

    /**
     * Retrieves all of the ClientServers from the Pterodactyl instance
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link java.util.List List} of {@link ClientServer ClientServers}
     */
    PteroAction<List<ClientServer>> retrieveServers();

    /**
     * Retrieves an individual ClientServer represented by the provided identifier from Pterodactyl instance
     *
     * @param  identifier
     *         The server identifier (first 8 characters of the uuid)
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @throws NotFoundException
     * 		   If the server cannot be found
     *
     * @return {@link PteroAction PteroAction} - Type {@link ClientServer ClientServer}
     */
    PteroAction<ClientServer> retrieveServerByIdentifier(String identifier);

    /**
     * Retrieves ClientServers matching the provided name from Pterodactyl instance
     *
     * @param  name
     *         The name
     * @param caseSensetive
     * 		   True - If P4J should search using case sensitivity
     *
     * @throws LoginException
     *         If the API key is incorrect
     *
     * @return {@link PteroAction PteroAction} - Type {@link java.util.List List} of {@link ClientServer ClientServers}
     */
    PteroAction<List<ClientServer>> retrieveServersByName(String name, boolean caseSensetive);

}
