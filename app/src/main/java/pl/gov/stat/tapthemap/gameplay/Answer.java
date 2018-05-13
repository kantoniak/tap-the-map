package pl.gov.stat.tapthemap.gameplay;

import pl.gov.stat.tapthemap.Country;

/**
 * Class representing answer.
 */
public class Answer {
    private Question question;
    private Country country;
    private String value;
    private double valueRaw;
    private int color;

    Answer(Question question, Country country, String value, double valueRaw, int color) {
        this.question = question;
        this.country = country;
        this.value = value;
        this.valueRaw = valueRaw;
        this.color = color;
    }

    /**
     * Get raw value.
     *
     * @return Double with raw value
     */
    public double getValueRaw() {
        return valueRaw;
    }

    /**
     * Get country.
     *
     * @return Country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Get value.
     *
     * @return Value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get color.
     *
     * @return Color
     */
    public int getColor() {
        return color;
    }

    /**
     * Check whether the answer is correct.
     *
     * @return True if answer is correct.
     */
    public boolean isCorrect() {
        return question.getIsCorrectAnswer(country);
    }
}