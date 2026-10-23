package com.itsmcodez.themoviedbapi.model.tv.core.credits;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itsmcodez.themoviedbapi.model.tv.series.Role;
import com.itsmcodez.themoviedbapi.util.JsonUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AggregateCastRolesDeserializerTest {
    @Test
    public void deserializesNormalRoleArray() throws JsonProcessingException {
        AggregateCast cast = readCast("""
            {"roles":[{"credit_id":"abc","character":"Character A","episode_count":10}]}
            """);

        assertRoles(cast.getRoles(), "abc");
        assertEquals("Character A", cast.getRoles().getFirst().getCharacter());
    }

    @Test
    public void flattensNestedAndDeeperRoleArraysInFirstSeenOrder() throws JsonProcessingException {
        AggregateCast cast = readCast("""
            {"roles":[[{"credit_id":"abc","character":"A","episode_count":10}],[[{"credit_id":"def","character":"B","episode_count":5}]]]]}
            """);

        assertRoles(cast.getRoles(), "abc", "def");
    }

    @Test
    public void treatsEmptyNullAndMissingRolesAsEmptyLists() throws JsonProcessingException {
        assertEmptyRoles("{\"roles\":[]}");
        assertEmptyRoles("{\"roles\":null}");
        assertEmptyRoles("{}");
    }

    @Test
    public void deduplicatesByCreditIdAcrossNestingLevels() throws JsonProcessingException {
        AggregateCast cast = readCast("""
            {"roles":[{"credit_id":"abc","character":"First","episode_count":1},[[{"credit_id":"abc","character":"Changed","episode_count":2}]],{"credit_id":"def","character":"Other","episode_count":3}]}
            """);

        assertRoles(cast.getRoles(), "abc", "def");
        assertEquals("First", cast.getRoles().getFirst().getCharacter());
    }

    @Test
    public void deduplicatesMissingCreditIdsUsingCharacterAndEpisodeCount() throws JsonProcessingException {
        AggregateCast cast = readCast("""
            {"roles":[[{"character":"Yeon Si-eun","episode_count":16}],[{"character":"Yeon Si-eun","episode_count":16}],{"character":"Yeon Si-eun","episode_count":8}]}
            """);

        assertEquals(2, cast.getRoles().size());
        assertEquals(Integer.valueOf(16), cast.getRoles().getFirst().getEpisodeCount());
        assertEquals(Integer.valueOf(8), cast.getRoles().get(1).getEpisodeCount());
    }

    @Test
    public void retainsDistinctRolesWithoutAnyDeduplicationIdentity() throws JsonProcessingException {
        AggregateCast cast = readCast("{\"roles\":[{},{}]}");

        assertEquals(2, cast.getRoles().size());
    }

    @Test
    public void rejectsMalformedRoleEntries() {
        assertThrows(JsonProcessingException.class, () -> readCast("{\"roles\":[42]}"));
        assertThrows(JsonProcessingException.class,
            () -> readCast("{\"roles\":[{\"episode_count\":{\"invalid\":true}}]}"));
    }

    @Test
    public void preservesOtherAggregateCastProperties() throws JsonProcessingException {
        AggregateCredits credits = JsonUtil.OBJECT_MAPPER.readValue("""
            {"cast":[{"id":7,"name":"Actor","order":2,"total_episode_count":10,"roles":[[{"credit_id":"abc","character":"Character A","episode_count":10}]]}],"crew":[]}
            """, AggregateCredits.class);

        assertNotNull(credits.getCast());
        assertFalse(credits.getCast().isEmpty());
        AggregateCast cast = credits.getCast().getFirst();
        assertEquals("Actor", cast.getName());
        assertEquals(Integer.valueOf(2), cast.getOrder());
        assertEquals(Integer.valueOf(10), cast.getTotalEpisodeCount());
        assertRoles(cast.getRoles(), "abc");
    }

    private static AggregateCast readCast(String json) throws JsonProcessingException {
        return JsonUtil.OBJECT_MAPPER.readValue(json, AggregateCast.class);
    }

    private static void assertEmptyRoles(String json) throws JsonProcessingException {
        AggregateCast cast = readCast(json);
        assertNotNull(cast.getRoles());
        assertTrue(cast.getRoles().isEmpty());
    }

    private static void assertRoles(List<Role> roles, String... creditIds) {
        assertEquals(creditIds.length, roles.size());
        for (int index = 0; index < creditIds.length; index++) {
            assertEquals(creditIds[index], roles.get(index).getCreditId());
        }
    }
}
