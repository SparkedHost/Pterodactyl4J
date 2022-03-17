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

import com.sparkedhost.pterodactyl4j.PteroAction;
import com.sparkedhost.pterodactyl4j.client.managers.APIKeyAction;
import com.sparkedhost.pterodactyl4j.client.managers.AccountManager;
import com.sparkedhost.pterodactyl4j.entities.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public interface Account extends User {

    String getFirstName();
    String getLastName();
    default String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }
    long getId();
    boolean isRootAdmin();
    String getLanguage();
    default Locale getLocale() {
        return Locale.forLanguageTag(getLanguage());
    }

    PteroAction<List<APIKey>> retrieveAPIKeys();

    default PteroAction<Optional<APIKey>> retrieveAPIKeyByIdentifier(String identifier) {
        return retrieveAPIKeys().map(List::stream)
                .map(stream -> stream.filter(key -> key.getIdentifier().equals(identifier))).map(Stream::findFirst);
    }

    APIKeyAction createAPIKey();

    AccountManager getManager();

}
