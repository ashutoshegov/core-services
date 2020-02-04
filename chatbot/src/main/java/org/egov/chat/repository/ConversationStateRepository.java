package org.egov.chat.repository;

import org.egov.chat.models.ConversationState;
import org.egov.chat.repository.querybuilder.ConversationStateQueryBuilder;
import org.egov.chat.repository.rowmapper.ConversationStateResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationStateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private ConversationStateResultSetExtractor conversationStateResultSetExtractor;

    private static final String insertNewConversationQuery = "INSERT INTO eg_chat_conversation_state " +
            "(conversation_id, user_id, active, locale) VALUES (?, ?, ?, ?)";

    private static final String updateConversationStateQuery = "UPDATE eg_chat_conversation_state SET active_node_id=? " +
            "question_details=? WHERE conversation_id=?";

    private static final String updateActiveStateForConversationQuery = "UPDATE eg_chat_conversation_state SET " +
            "active=FALSE WHERE conversation_id=?";

    private static final String selectActiveNodeIdForConversationStateQuery = "SELECT (active_node_id " +
            ") FROM eg_chat_conversation_state WHERE conversation_id=?";

    private static final String selectConversationStateForIdQuery = "SELECT * FROM eg_chat_conversation_state WHERE " +
            "conversation_id=?";

    private static final String selectActiveConversationStateForUserIdQuery = "SELECT * FROM eg_chat_conversation_state WHERE " +
            "user_id=? AND active=TRUE";

    private static final String selectCountConversationStateForUserIdQuery = "SELECT count(*) FROM eg_chat_conversation_state WHERE " +
            "user_id=?";

    public int insertNewConversation(ConversationState conversationState) {
        return jdbcTemplate.update(insertNewConversationQuery,
                conversationState.getConversationId(),
                conversationState.getUserId(),
                conversationState.isActive(),
                conversationState.getLocale());
    }

    public int updateConversationStateForId(ConversationState conversationState) {
        return namedParameterJdbcTemplate.update(ConversationStateQueryBuilder.UPDATE_CONVERSATION_STATE_QUERY,
                ConversationStateQueryBuilder.getParametersForConversationStateUpdate(conversationState));
    }

    public int markConversationInactive(String conversationId) {
        return jdbcTemplate.update(updateActiveStateForConversationQuery, conversationId);
    }

    public String getActiveNodeIdForConversation(String conversationId) {
        return  (jdbcTemplate.queryForObject(selectActiveNodeIdForConversationStateQuery, new Object[] { conversationId },
                String.class));
    }

    public ConversationState getActiveConversationStateForUserId(String userId) {
        try {
            return jdbcTemplate.query(selectActiveConversationStateForUserIdQuery, new Object[]{ userId },
                    conversationStateResultSetExtractor);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int getConversationStateCountForUserId(String userId) {
        return  (jdbcTemplate.queryForObject(selectCountConversationStateForUserIdQuery, new Object[] { userId },
                Integer.class));
    }

    public ConversationState getConversationStateForId(String conversationId) {
        return jdbcTemplate.query(selectConversationStateForIdQuery, new Object[] { conversationId },
                conversationStateResultSetExtractor);
    }


}
