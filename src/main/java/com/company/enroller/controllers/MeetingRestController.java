package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {

        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@Valid @RequestBody Meeting meeting) {
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{meetingId}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipants(@PathVariable long meetingId, @RequestBody String participantToAdd) {
        Participant participant = participantService.findByLogin(participantToAdd);
            if (participant == null) {
                return new ResponseEntity<>("Participant with login \"" + participantToAdd
                    + "\" doesn't exist", HttpStatus.NOT_FOUND);
            }
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting with id \"" + meetingId
                + "\" doesn't exist", HttpStatus.NOT_FOUND);
        }
        meetingService.addParticipant(meeting, participant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/removeParticipant/{meetingId}", method = RequestMethod.POST)
    public ResponseEntity<?> deleteParticipant(@PathVariable long meetingId, @RequestBody String participantToRemove) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting with id \"" + meetingId
                + "\" doesn't exist", HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(participantToRemove);
        if (participant == null) {
            return new ResponseEntity<>("Participant with login \"" + participantToRemove
                + "\" doesn't exist", HttpStatus.NOT_FOUND);
        }
        if (!meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("Participant with login \"" + participantToRemove
                + "\" is not assigned to this meeting", HttpStatus.NOT_FOUND);
        }

        meetingService.removeParticipant(meeting, participant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("meetingId") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting with id \"" + meetingId
                + "\" doesn't exist", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
