package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.VacancyRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;

@Service
@ThreadSafe
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        if (image != null && image.getContent() != null && image.getContent().length > 0) {
            saveNewFile(candidate, image);
        }
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        var file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            candidateRepository.deleteById(id);
            fileService.deleteById(fileOptional.get().getFileId());
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        if (image != null && image.getContent() != null && image.getContent().length > 0) {
            var oldFileId = candidate.getFileId();
            saveNewFile(candidate, image);
            boolean isUpdated = candidateRepository.update(candidate);
            if (isUpdated) {
                fileService.deleteById(oldFileId);
            }
            return isUpdated;
        }
        return candidateRepository.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }

}
