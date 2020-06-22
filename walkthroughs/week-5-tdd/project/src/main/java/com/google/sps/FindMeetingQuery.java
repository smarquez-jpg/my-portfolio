// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();
    if(events.equals(Collections.emptySet())) return Arrays.asList(TimeRange.WHOLE_DAY);
    //create List freeTime to hold the free time before and after an event
    List<TimeRange> freeTime = new ArrayList<>();
    for(Event event : events){
      TimeRange beforeEvent = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, event.getWhen().start(), false);
      TimeRange afterEvent = TimeRange.fromStartEnd(event.getWhen().end(), TimeRange.END_OF_DAY, true);
      Set<String> modifiedAttendees = new HashSet<>(request.getAttendees());
      modifiedAttendees.retainAll(event.getAttendees());
      Set<String> optionalAttendees = new HashSet<>(request.getOptionalAttendees());
      if(modifiedAttendees.size() >= 1){
        freeTime.add(beforeEvent);
        freeTime.add(afterEvent);
      } else {
        TimeRange allDay = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
        freeTime.add(allDay);
      }
    }
    //create List of updated options that take into account different circumstances
    //ex: overlapping events
    List<TimeRange> updatedOptions = new ArrayList<>();
    for(int i = 0; i < freeTime.size(); i++){
      TimeRange firstTime = freeTime.get(i);
      for(int j = i+1; j < freeTime.size(); j++){
        TimeRange secondTime = freeTime.get(j);
        if(firstTime.overlaps(secondTime)){
          int startTime = Math.max(firstTime.start(), secondTime.start());
          int endTime = Math.min(firstTime.end(), secondTime.end());
          TimeRange newOption = TimeRange.fromStartEnd(startTime, endTime, false);
          if(!updatedOptions.contains(newOption) && startTime != endTime) updatedOptions.add(newOption);
        }
      }
    }
    //if there is anything in updatedOptions, return those options
    if(updatedOptions.size() == 0) {
      for(int i = 0; i < freeTime.size(); i++){
        TimeRange time = freeTime.get(i);
        if(time.duration() < request.getDuration()) freeTime.remove(time);
      }    
      return freeTime;
    } else {
      for(int i = 0; i < updatedOptions.size(); i++){
        TimeRange time = updatedOptions.get(i);
        if(time.duration() < request.getDuration()) updatedOptions.remove(time);
      } 
      return updatedOptions;
    }
  }
}