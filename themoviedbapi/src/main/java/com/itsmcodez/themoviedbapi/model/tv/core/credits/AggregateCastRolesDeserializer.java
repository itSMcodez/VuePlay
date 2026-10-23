package com.itsmcodez.themoviedbapi.model.tv.core.credits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.itsmcodez.themoviedbapi.model.tv.series.Role;

/**
 * Deserializes aggregate cast roles returned by TMDB as either role objects or nested arrays of role objects.
 */
public class AggregateCastRolesDeserializer extends JsonDeserializer<List<Role>> {
    @Override
    public List<Role> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }

        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return new ArrayList<>();
        }

        if (parser.currentToken() != JsonToken.START_ARRAY) {
            context.reportInputMismatch(List.class, "Expected an array for aggregate cast roles but got %s", parser.currentToken());
        }

        List<Role> roles = new ArrayList<>();
        Set<RoleKey> seenRoles = new HashSet<>();
        readRolesArray(parser, context, roles, seenRoles);
        return roles;
    }

    @Override
    public List<Role> getNullValue(DeserializationContext context) {
        return new ArrayList<>();
    }

    private void readRolesArray(JsonParser parser, DeserializationContext context, List<Role> roles, Set<RoleKey> seenRoles)
        throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.START_ARRAY) {
                readRolesArray(parser, context, roles, seenRoles);
            }
            else if (parser.currentToken() == JsonToken.START_OBJECT) {
                Role role = context.readValue(parser, Role.class);
                addRole(role, roles, seenRoles);
            }
            else {
                context.reportInputMismatch(Role.class,
                    "Expected a role object or nested array in aggregate cast roles but got %s", parser.currentToken());
            }
        }
    }

    private void addRole(Role role, List<Role> roles, Set<RoleKey> seenRoles) {
        RoleKey key = RoleKey.from(role);
        if (key == null || seenRoles.add(key)) {
            roles.add(role);
        }
    }

    private record RoleKey(boolean hasCreditId, String creditId, String character, Integer episodeCount) {
        private static RoleKey from(Role role) {
            if (role.getCreditId() != null) {
                return new RoleKey(true, role.getCreditId(), null, null);
            }

            if (role.getCharacter() != null || role.getEpisodeCount() != null) {
                return new RoleKey(false, null, role.getCharacter(), role.getEpisodeCount());
            }

            return null;
        }
    }
}
