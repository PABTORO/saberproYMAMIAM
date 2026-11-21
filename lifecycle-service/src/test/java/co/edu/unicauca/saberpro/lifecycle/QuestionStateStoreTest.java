package co.edu.unicauca.saberpro.lifecycle;

import co.edu.unicauca.saberpro.lifecycle.domain.QuestionState;
import co.edu.unicauca.saberpro.lifecycle.domain.QuestionStateStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionStateStoreTest {

    @Test
    void unknownQuestionIsDraft() {
        QuestionStateStore store = new QuestionStateStore();
        assertEquals(QuestionState.DRAFT, store.current(1L));
        assertFalse(store.isKnown(1L));
    }

    @Test
    void submitMovesDraftToPendingReview() {
        QuestionStateStore store = new QuestionStateStore();
        assertTrue(store.submitForReview(1L));
        assertEquals(QuestionState.PENDING_REVIEW, store.current(1L));
    }

    @Test
    void secondSubmitIsIdempotent() {
        QuestionStateStore store = new QuestionStateStore();
        store.submitForReview(1L);
        assertFalse(store.submitForReview(1L));
        assertEquals(QuestionState.PENDING_REVIEW, store.current(1L));
    }
}
