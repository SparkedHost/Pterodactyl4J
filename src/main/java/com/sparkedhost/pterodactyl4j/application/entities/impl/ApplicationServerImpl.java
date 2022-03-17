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

import com.sparkedhost.pterodactyl4j.PteroAction;
import com.sparkedhost.pterodactyl4j.ServerStatus;
import com.sparkedhost.pterodactyl4j.application.entities.*;
import com.sparkedhost.pterodactyl4j.application.managers.*;
import com.sparkedhost.pterodactyl4j.application.entities.*;
import com.sparkedhost.pterodactyl4j.entities.FeatureLimit;
import com.sparkedhost.pterodactyl4j.entities.Limit;
import com.sparkedhost.pterodactyl4j.entities.impl.FeatureLimitImpl;
import com.sparkedhost.pterodactyl4j.entities.impl.LimitImpl;
import com.sparkedhost.pterodactyl4j.requests.PteroActionImpl;
import com.sparkedhost.pterodactyl4j.requests.Route;
import com.sparkedhost.pterodactyl4j.utils.Relationed;
import com.sparkedhost.pterodactyl4j.application.managers.*;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.*;

public class ApplicationServerImpl implements ApplicationServer {

	private final PteroApplicationImpl impl;
	private final JSONObject json;
	private final JSONObject relationships;

	public ApplicationServerImpl(PteroApplicationImpl impl, JSONObject json) {
		this.impl = impl;
		this.json = json.getJSONObject("attributes");
		this.relationships = json.getJSONObject("attributes").optJSONObject("relationships");
	}

	@Override
	public String getExternalId() {
		return json.optString("external_id");
	}

	@Override
	public UUID getUUID() {
		return UUID.fromString(json.getString("uuid"));
	}

	@Override
	public String getIdentifier() {
		return json.getString("identifier");
	}

	@Override
	public String getName() {
		return json.getString("name");
	}

	@Override
	public String getDescription() {
		return json.getString("description");
	}

	@Override
	public boolean isSuspended() {
		return json.getBoolean("suspended");
	}

	@Override
	public Limit getLimits() {
		return new LimitImpl(json.getJSONObject("limits"));
	}

	@Override
	public FeatureLimit getFeatureLimits() {
		return new FeatureLimitImpl(json.getJSONObject("feature_limits"));
	}

	@Override
	public Relationed<ApplicationUser> getOwner() {
		return new Relationed<ApplicationUser>() {
			@Override
			public PteroAction<ApplicationUser> retrieve() {
				return impl.retrieveUserById(getOwnerIdLong());
			}

			@Override
			public Optional<ApplicationUser> get() {
				if(!json.has("relationships")) return Optional.empty();
				return Optional.of(new ApplicationUserImpl(relationships.getJSONObject("user"), impl));
			}
		};
	}

	@Override
	public long getOwnerIdLong() {
		return json.getLong("user");
	}

	@Override
	public Relationed<Node> getNode() {
		return new Relationed<Node>() {
			@Override
			public PteroAction<Node> retrieve() {
				return impl.retrieveNodeById(getNodeIdLong());
			}

			@Override
			public Optional<Node> get() {
				if(!json.has("relationships")) return Optional.empty();
				return Optional.of(new NodeImpl(relationships.getJSONObject("node"), impl));
			}
		};
	}

	@Override
	public long getNodeIdLong() {
		return json.getLong("node");
	}

	@Override
	public Optional<List<ApplicationAllocation>> getAllocations() {
		if(!json.has("relationships")) return Optional.empty();
		List<ApplicationAllocation> allocations = new ArrayList<>();
		JSONObject json = relationships.getJSONObject("allocations");
		for(Object o : json.getJSONArray("data")) {
			JSONObject allocation = new JSONObject(o.toString());
			allocations.add(new ApplicationAllocationImpl(allocation, impl));
		}
		return Optional.of(Collections.unmodifiableList(allocations));
	}

	@Override
	public Relationed<ApplicationAllocation> getDefaultAllocation() {
		return new Relationed<ApplicationAllocation>() {
			@Override
			public PteroAction<ApplicationAllocation> retrieve() {
				return impl.retrieveAllocationById(getDefaultAllocationIdLong());
			}

			@Override
			public Optional<ApplicationAllocation> get() {
				if(!json.has("relationships")) return Optional.empty();
				List<ApplicationAllocation> allocations = getAllocations().get();
				for (ApplicationAllocation a : allocations) {
					if (a.getIdLong() == getDefaultAllocationIdLong()) {
						return Optional.of(a);
					}
				}
				return Optional.empty();
			}
		};
	}

	@Override
	public long getDefaultAllocationIdLong() {
		return json.getLong("allocation");
	}

	@Override
	public Relationed<Nest> getNest() {
		return new Relationed<Nest>() {
			@Override
			public PteroAction<Nest> retrieve() {
				return impl.retrieveNestById(getNestIdLong());
			}

			@Override
			public Optional<Nest> get() {
				if(!json.has("relationships")) return Optional.empty();
				return Optional.of(new NestImpl(relationships.getJSONObject("nest"), impl));
			}
		};
	}

	@Override
	public long getNestIdLong() {
		return json.getLong("nest");
	}

	@Override
	public Relationed<ApplicationEgg> getEgg() {
		return new Relationed<ApplicationEgg>() {
			@Override
			public PteroAction<ApplicationEgg> retrieve() {
				return impl.retrieveEggById(getNest().retrieve().execute(), getEggIdLong());
			}

			@Override
			public Optional<ApplicationEgg> get() {
				if(!json.has("relationships")) return Optional.empty();
				return Optional.of(new ApplicationEggImpl(relationships.getJSONObject("egg"), impl));
			}
		};
	}

	@Override
	public long getEggIdLong() {
		return json.getLong("egg");
	}

	@Override
	public ServerStatus getStatus() {
		if (json.isNull("status"))
			return ServerStatus.UNKNOWN;
		return ServerStatus.valueOf(json.getString("status").toUpperCase());
	}

	@Override
	public ServerDetailManager getDetailManager() {
		return new ServerDetailManagerImpl(this, impl);
	}

	@Override
	public ServerBuildManager getBuildManager() {
		return new ServerBuildManagerImpl(this, impl);
	}

	@Override
	public ServerStartupManager getStartupManager() {
		return new ServerStartupManagerImpl(this, impl);
	}

	@Override
	public ServerManager getManager() {
		return new ServerManager(this);
	}

	@Override
	public ServerController getController() {
		return new ServerController(this, impl);
	}

	@Override
	public Relationed<List<ApplicationDatabase>> getDatabases() {
		ApplicationServer server = this;
		return new Relationed<List<ApplicationDatabase>>() {
			@Override
			public PteroAction<List<ApplicationDatabase>> retrieve() {
				return PteroActionImpl.onRequestExecute(impl.getP4J(), Route.Databases.LIST_DATABASES.compile(getId()),
						(response, request) -> {
							JSONObject json = response.getObject();
							List<ApplicationDatabase> databases = new ArrayList<>();
							for (Object o : json.getJSONArray("data")) {
								JSONObject database = new JSONObject(o.toString());
								databases.add(new ApplicationDatabaseImpl(database, server, impl));
							}
							return Collections.unmodifiableList(databases);
						});
			}

			@Override
			public Optional<List<ApplicationDatabase>> get() {
				if(!json.has("relationships")) return Optional.empty();
				JSONObject json = relationships.getJSONObject("databases");
				List<ApplicationDatabase> databases = new ArrayList<>();
				for (Object o : json.getJSONArray("data")) {
					JSONObject database = new JSONObject(o.toString());
					databases.add(new ApplicationDatabaseImpl(database, server, impl));
				}
				return Optional.of(Collections.unmodifiableList(databases));
			}
		};
	}

	@Override
	public PteroAction<ApplicationDatabase> retrieveDatabaseById(String id) {
		return PteroActionImpl.onRequestExecute(impl.getP4J(), Route.Databases.GET_DATABASE.compile(getId(), id),
				(response, request) -> new ApplicationDatabaseImpl(response.getObject(), this, impl));
	}

	@Override
	public ApplicationDatabaseManager getDatabaseManager() {
		return new ApplicationDatabaseManagerImpl(this, impl);
	}

	@Override
	public Container getContainer() {
		return new ContainerImpl(json.getJSONObject("container"));
	}

	@Override
	public long getIdLong() {
		return json.getLong("id");
	}

	@Override
	public OffsetDateTime getCreationDate() {
		return OffsetDateTime.parse(json.optString("created_at"));
	}

	@Override
	public OffsetDateTime getUpdatedDate() {
		return OffsetDateTime.parse(json.optString("updated_at"));
	}

	@Override
	public String toString() {
		return json.toString(4);
	}
}
