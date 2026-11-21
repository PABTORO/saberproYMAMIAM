package co.edu.unicauca.saberpro.lifecycle.api;

import co.edu.unicauca.saberpro.lifecycle.domain.QuestionState;
import co.edu.unicauca.saberpro.lifecycle.domain.QuestionStateStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Endpoints de solo lectura para verificar el estado gestionado por este servicio. */
@RestController
@RequestMapping("/lifecycle/questions")
public class LifecycleController {

    private final QuestionStateStore store;

    public LifecycleController(QuestionStateStore store) {
        this.store = store;
    }

    @GetMapping
    public Map<Long, QuestionState> all() {
        return store.snapshot();
    }

    /** 200 con el estado si la pregunta ya tuvo una transicion; 404 si este servicio aun no la conoce. */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> state(@PathVariable Long id) {
        if (!store.isKnown(id)) {
            return ResponseEntity.status(404).body(Map.of(
                    "questionId", id,
                    "message", "Sin transiciones registradas (la pregunta se considera DRAFT)"));
        }
        return ResponseEntity.ok(Map.of("questionId", id, "state", store.current(id).name()));
    }
}
