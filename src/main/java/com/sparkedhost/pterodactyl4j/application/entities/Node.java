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
import com.sparkedhost.pterodactyl4j.application.managers.ApplicationAllocationManager;
import com.sparkedhost.pterodactyl4j.application.managers.NodeAction;
import com.sparkedhost.pterodactyl4j.utils.Relationed;

import java.util.List;
import java.util.UUID;

public interface Node extends ISnowflake {

	boolean isPublic();
	String getName();
	String getDescription();
	Relationed<Location> getLocation();
	ApplicationAllocationManager getAllocationManager();
	String getFQDN();
	String getScheme();
	boolean isBehindProxy();
	boolean hasMaintanceMode();
	default String getMemory() { return Long.toUnsignedString(getMemoryLong()); }
	long getMemoryLong();
	default String getMemoryOverallocate() { return Long.toUnsignedString(getMemoryOverallocateLong()); }
	long getMemoryOverallocateLong();
	default String getDisk() { return Long.toUnsignedString(getDiskLong()); }
	long getDiskLong();
	default String getDiskOverallocate() { return  Long.toUnsignedString(getDiskOverallocateLong()); }
	long getDiskOverallocateLong();
	default String getUploadLimit() { return Long.toUnsignedString(getUploadLimitLong()); }
	long getUploadLimitLong();
	long getAllocatedMemoryLong();
	default String getAllocatedMemory() { return Long.toUnsignedString(getAllocatedMemoryLong()); }
	long getAllocatedDiskLong();
	default String getAllocatedDisk() { return Long.toUnsignedString(getAllocatedDiskLong()); }
	String getDaemonListenPort();
	String getDaemonSFTPPort();
	String getDaemonBase();
	Relationed<List<ApplicationServer>> getServers();
	Relationed<List<ApplicationAllocation>> getAllocations();
	Relationed<List<ApplicationAllocation>> getAllocationsByPort(int port);
	default Relationed<List<ApplicationAllocation>> getAllocationsByPort(String port) {
		return getAllocationsByPort(Integer.parseUnsignedInt(port));
	}
	PteroAction<Configuration> retrieveConfiguration();

	NodeAction edit();
	PteroAction<Void> delete();

	interface Configuration {
		boolean isDebug();
		UUID getUUID();
		String getTokenId();
		String getToken();
		APIConfiguration getAPI();
		SystemConfiguration getSystem();
		List<String> getAllowedMounts();
		String getRemote();
	}

	interface APIConfiguration {
		String getHost();
		int getPort();
		boolean isSSLEnabled();
		String getSSLCertPath();
		String getSSLKeyPath();
		int getUploadLimit();
	}

	interface SystemConfiguration {
		String getDataPath();
		int getSFTPPort();
	}



}
