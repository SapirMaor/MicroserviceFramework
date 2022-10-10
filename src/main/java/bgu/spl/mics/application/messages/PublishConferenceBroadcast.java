package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;

public class PublishConferenceBroadcast implements Broadcast {

    private final ConferenceInformation conferenceInformation;
    public PublishConferenceBroadcast(ConferenceInformation conferenceInformation){
        this.conferenceInformation = conferenceInformation;
    }

    public ConferenceInformation getConferenceInformation() {
        return conferenceInformation;
    }
}
