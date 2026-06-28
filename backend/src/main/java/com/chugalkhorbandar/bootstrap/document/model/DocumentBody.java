package com.chugalkhorbandar.bootstrap.document.model;

import java.util.List;

public record DocumentBody(String heading, List<DocumentSection> sections) {}
