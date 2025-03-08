package be.md;

import lombok.Builder;

@Builder
public class Metadata {
    Integer diagramsPerRow;
    boolean mirror;
    boolean addPageNumbers;
    SupportedLanguage language;

}
