package be.md;

public enum SupportedLanguage {
    en,nl,fr,none;

    @Override
    public String toString() {
        if(this == none)return "Don't add text.";
        return super.toString();
    }
}
