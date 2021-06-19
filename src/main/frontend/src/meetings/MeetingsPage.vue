<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length == 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
      Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list :meetings="meetings"
                   :username="username"
                   @attend="addMeetingParticipant($event)"
                   @unattend="removeMeetingParticipant($event)"
                   @delete="deleteMeeting($event)"></meetings-list>
    <span v-if="avaliableMeetings.length == 0">
               Brak dostępnych spotkań.
           </span>
    <h3 v-else>
      Dostępne zajęcia ({{ avaliableMeetings.length }})
    </h3>
    <meetings-list :meetings="avaliableMeetings"
                   :username="username"
                   @attend="addMeetingParticipant($event)"
                   @unattend="removeMeetingParticipant($event)"
                   @delete="deleteMeeting($event)"></meetings-list>
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";

    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: [],
                avaliableMeetings: []
            };
        },
        methods: {
            addNewMeeting(meeting) {
                this.$http.post('meetings', meeting)
                  .then(response => {
                    this.avaliableMeetings.push(response.body);
                  })
                  .catch((error) => console.log(error));
            },
            addMeetingParticipant(meeting) {
              this.$http.post(`meetings/${meeting.id}`, this.username)
                  .then(response => {
                    meeting.participants.push(this.username);
                    this.avaliableMeetings.splice(this.meetings.indexOf(meeting), 1);
                    this.meetings.push(meeting);
                  })
                  .catch(response => console.log(response.status));
            },
            removeMeetingParticipant(meeting) {
              this.$http.post(`meetings/removeParticipant/${meeting.id}`, this.username)
                  .then(response => {
                    meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
                    this.meetings.splice(this.meetings.indexOf(meeting), 1);
                    this.avaliableMeetings.push(meeting);
                  })
                  .catch(response => console.log(response.status));
            },
            deleteMeeting(meeting) {
              this.$http.delete(`meetings/${meeting.id}`)
                  .then(response => {
                    this.avaliableMeetings.splice(this.avaliableMeetings.indexOf(meeting), 1);
                  })
                  .catch(response => console.log(response.status));
            },
            getMeetings() {
              this.$http.get('meetings')
                  .then(response => {
                    for (let i=0; i < response.body.length; i++) {
                      let participant = [];
                      for (let j=0; j<response.body[i].participants.length; j++) {
                        participant.push(response.body[i].participants[j].login);
                      }
                      response.body[i].participants = participant;
                      if (response.body[i].participants.indexOf(this.username) < 0) {
                        this.avaliableMeetings.push(response.body[i])
                      } else {
                        this.meetings.push(response.body[i])
                      }
                    }
                  })
                  .catch(response => console.log(response.status));
            },
        },
      beforeMount() {
          this.getMeetings();
      }
    }
</script>
