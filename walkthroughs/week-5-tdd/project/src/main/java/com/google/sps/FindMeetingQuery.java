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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if(events.equals(Collections.emptySet())) return Arrays.asList(TimeRange.WHOLE_DAY);
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();
    Collection<TimeRange> options = new ArrayList<>();
    for(Event event : events){
      TimeRange beforeEvent = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, event.getWhen().start(), false);
      options.add(beforeEvent);
      TimeRange afterEvent = TimeRange.fromStartEnd(event.getWhen().end(), TimeRange.END_OF_DAY, true);
      options.add(afterEvent);
    }
    Collection<TimeRange> updatedOptions = new ArrayList<>();
    for(TimeRange firstTime : options){
      for(TimeRange secondTime : options){
        if(firstTime.overlaps(secondTime) && !(firstTime.equals(secondTime))){
          TimeRange newOption = TimeRange.fromStartEnd(Math.max(firstTime.start(), secondTime.start()), Math.min(firstTime.end(), secondTime.end()), false);
          if(!updatedOptions.contains(newOption)) updatedOptions.add(newOption);
        }
      }
    }
    if(updatedOptions.size() == 0) return options;
    else return updatedOptions;
  }
}
