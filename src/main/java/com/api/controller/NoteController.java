package com.api.controller;

import com.api.model.Note;
import com.api.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Controller
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String UPLOAD_DIR = "uploads/";

    // Route to join a note room
    @GetMapping("/{username}/{group}")
    public String joinRoom(@PathVariable String username,
                           @PathVariable String group,
                           Model model) {
        model.addAttribute("username", username);
        model.addAttribute("group", group);
        return "note-room";
    }

    // WebSocket handler for note sharing
    @MessageMapping("/note/{groupName}")
    public void shareNote(@DestinationVariable String groupName, @Payload Note note) {
        note.setGroupName(groupName);
        noteRepository.save(note);
        messagingTemplate.convertAndSend("/topic/notes/" + groupName, note);
    }

    // Upload media file (image/video)
    @PostMapping("/upload")
    @ResponseBody
    public String handleUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.getSize() > 50 * 1024 * 1024) {
            return "File too large. Max 50MB allowed.";
        }

        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
        Files.createDirectories(uploadPath);

        String fileName = System.currentTimeMillis() + "_" +
                file.getOriginalFilename().replaceAll("\\s+", "_");

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        System.out.println("Uploaded file: " + filePath.toAbsolutePath());
        return "/uploads/" + fileName;
    }
}
