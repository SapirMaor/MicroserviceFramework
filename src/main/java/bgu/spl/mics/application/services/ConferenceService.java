package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    ConferenceInformation conferenceInformation;

    public ConferenceService(String name, ConferenceInformation conferenceInformation) {
        super(name);
        this.conferenceInformation = conferenceInformation;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (c) -> {
            conferenceInformation.updateDate();
            if(conferenceInformation.getDate() == 0){
                sendBroadcast(new PublishConferenceBroadcast(conferenceInformation));
                terminate();
            }
        });
        subscribeEvent(PublishResultsEvent.class, (addModel) ->
                conferenceInformation.add(addModel.getModel()));
    }
}
