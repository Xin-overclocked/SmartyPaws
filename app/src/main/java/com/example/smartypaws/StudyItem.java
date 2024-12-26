package com.example.smartypaws;

import com.google.firebase.Timestamp;

public interface StudyItem {
    String getTitle();
    String getDescription();
    void setLastAccessed(Timestamp timestamp);
    Timestamp getLastAccessed();
}
