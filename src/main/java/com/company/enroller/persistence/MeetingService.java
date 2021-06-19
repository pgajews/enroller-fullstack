package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

    DatabaseConnector connector;
    Session session;

    public MeetingService() {
        connector = DatabaseConnector.getInstance();
        session = connector.getSession();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public void add(Meeting meeting) {
        Transaction transaction = session.beginTransaction();
        session.save(meeting);
        transaction.commit();
    }

    public void addParticipant(Meeting meeting, Participant participant) {
        Transaction transaction = session.beginTransaction();
        meeting.addParticipant(participant);
        session.save(meeting);
        transaction.commit();
    }


    public Meeting findById(long meetingId) {
        return (Meeting) connector.getSession().get(Meeting.class, meetingId);
    }

    public void removeParticipant(Meeting meeting, Participant participant) {
        Transaction transaction = session.beginTransaction();
        meeting.removeParticipant(participant);
        session.save(meeting);
        transaction.commit();
    }

    public void delete(Meeting meeting) {
        Transaction transaction = session.beginTransaction();
        session.delete(meeting);
        transaction.commit();
    }

}
