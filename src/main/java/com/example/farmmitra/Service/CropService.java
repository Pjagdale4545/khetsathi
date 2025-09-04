package com.example.farmmitra.Service;

import com.example.farmmitra.dto.CropDto;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.Repository.CropRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    private final CropRepository cropRepository;

    @Autowired
    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }
    
    public Page<Crop> getCrops(int page, int size, String search) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), size > 0 ? size : 10);
        if (search != null && !search.isEmpty()) {
            return cropRepository.findByCropNameContainingIgnoreCase(search, pageable);
        } else {
            return cropRepository.findAll(pageable);
        }
    }

    public Crop saveCrop(CropDto cropDto, Farmer farmer) {
        if (farmer == null) {
            throw new IllegalArgumentException("Farmer cannot be null when saving a crop");
        }
        Crop crop = new Crop();
        crop.setCropName(cropDto.getCropName());
        crop.setDescription(cropDto.getDescription());
        crop.setQuantity(cropDto.getQuantity());
        crop.setPricePerUnit(cropDto.getPricePerUnit());
        crop.setFarmer(farmer);
        crop.setCreatedAt(LocalDateTime.now());
        return cropRepository.save(crop);
    }

    public List<Crop> findCropsByFarmer(Farmer farmer) {
        return cropRepository.findByFarmer(farmer);
    }

    public void deleteCrop(Long cropId) {
        cropRepository.deleteById(cropId);
    }
    
    public Optional<Crop> getCropById(Long cropId) {
        return cropRepository.findById(cropId);
    }

}
