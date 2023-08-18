package com.example.spacelab.controller;

import com.example.spacelab.model.dto.ContactInfoDTO;
import com.example.spacelab.service.ContactInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log
@RequiredArgsConstructor
@RequestMapping("/contacts")
public class ContactController {

    private final ContactInfoService contactService;

    @GetMapping
    public ResponseEntity<List<ContactInfoDTO>> getContacts(@RequestParam(required = false) Integer page,
                                                            @RequestParam(required = false) Integer size) {
        List<ContactInfoDTO> contactList;
        if(page == null || size == null) contactList = contactService.getContacts();
        else contactList = contactService.getContacts(PageRequest.of(page, size));

        return new ResponseEntity<>(contactList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactInfoDTO> getContact(@PathVariable Long id) {
        ContactInfoDTO info = contactService.getContact(id);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ContactInfoDTO> createNewContact(@Valid @RequestBody ContactInfoDTO contactInfoDTO) {
        ContactInfoDTO info = contactService.saveContact(contactInfoDTO);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactInfoDTO> editContact(@PathVariable Long id,
                                                      @Valid @RequestBody ContactInfoDTO contactInfoDTO) {
        ContactInfoDTO info = contactService.editContact(contactInfoDTO);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);

        return new ResponseEntity<>("Deleted successfully!", HttpStatus.OK);
    }
}