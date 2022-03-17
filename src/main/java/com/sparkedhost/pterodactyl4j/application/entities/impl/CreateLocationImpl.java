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

package com.sparkedhost.pterodactyl4j.application.entities.impl;

import com.sparkedhost.pterodactyl4j.requests.Route;
import com.sparkedhost.pterodactyl4j.requests.action.AbstractLocationAction;
import com.sparkedhost.pterodactyl4j.utils.Checks;
import okhttp3.RequestBody;
import org.json.JSONObject;

public class CreateLocationImpl extends AbstractLocationAction {

	CreateLocationImpl(PteroApplicationImpl impl) {
		super(impl, Route.Locations.CREATE_LOCATION.compile());
	}

	@Override
	protected RequestBody finalizeData() {
		Checks.notBlank(this.shortCode, "Shortcode");
		Checks.notBlank(this.description, "Description");
		JSONObject json = new JSONObject();
		json.put("short", this.shortCode);
		json.put("long", this.description);
		return getRequestBody(json);
	}
}
