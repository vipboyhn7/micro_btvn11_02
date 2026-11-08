package com.example.medicinecache.medicine;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository repository;

    public MedicineService(MedicineRepository repository) {
        this.repository = repository;
    }

    public List<Medicine> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "medicines", key = "#id")
    public Medicine findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));
    }

    @Transactional
    public Medicine create(Medicine medicine) {
        return repository.save(medicine);
    }

    @Transactional
    @CacheEvict(value = "medicines", key = "#medicine.id")
    public Medicine update(Long id, Medicine medicine) {
        Medicine existing = findById(id);
        existing.updateFrom(medicine);
        return repository.save(existing);
    }

    @Transactional
    @CacheEvict(value = "medicines", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new MedicineNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
