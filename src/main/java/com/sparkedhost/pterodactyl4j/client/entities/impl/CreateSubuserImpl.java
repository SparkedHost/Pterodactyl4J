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

package com.sparkedhost.pterodactyl4j.client.entities.impl;

import com.sparkedhost.pterodactyl4j.client.entities.ClientServer;
import com.sparkedhost.pterodactyl4j.client.managers.SubuserCreationAction;
import com.sparkedhost.pterodactyl4j.requests.Route;
import com.sparkedhost.pterodactyl4j.requests.action.AbstractSubuserAction;
import com.sparkedhost.pterodactyl4j.utils.Checks;
import com.sparkedhost.pterodactyl4j.requests.PteroActionImpl;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class CreateSubuserImpl extends AbstractSubuserAction implements SubuserCreationAction {

    private String email;

    public CreateSubuserImpl(ClientServer server, PteroClientImpl impl) {
        super(impl, Route.Subusers.CREATE_SUBUSER.compile(server.getUUID().toString()));
    }

    @Override
    public SubuserCreationAction setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    protected RequestBody finalizeData() {
        Checks.notBlank(this.email, "Email");
        Checks.notEmpty(this.permissions, "Permissions");
        JSONObject json = new JSONObject()
                .put("email", email)
                .put("permissions", permissions.stream().map(permission -> permission.getRaw()).collect(Collectors.toList()));
        return PteroActionImpl.getRequestBody(json);
    }
}
