package pl.kamhol1.todoapp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface SqlTaskRepository extends TaskRepository, JpaRepository<Task, Integer> {
    @Override
    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 FROM tasks WHERE id=?1")
    boolean existsById(Integer id);
}
