package tw.foxo.boise.upost.eventBus

class EventMessage (event:String,data:Any) {
    val eventName = event
    val eventData = data
}