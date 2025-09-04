package com.example.farmmitra.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Farmer;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByCropNameContainingIgnoreCase(String cropName);
    Page<Crop> findByCropNameContainingIgnoreCase(String cropName, Pageable pageable);
    List<Crop> findTop5ByOrderByCreatedAtDesc();
    List<Crop> findByFarmer(Farmer farmer);

}
