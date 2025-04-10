package repository;

import model.CartoesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartoesRepository extends JpaRepository<CartoesModel, Long> {
}