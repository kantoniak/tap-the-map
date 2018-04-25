package com.kantoniak.discrete_fox.ask;

/**
 * Class used to describe the category of a question.
 */
public enum QuestionCategory {
    GENERAL, ECONOMY, POPULATION, INDUSTRY, AGRICULTURE, TRADE, TRANSPORT, ENVIRONMENT, SCIENCE;

    /**
     * Color used while displaying low level of a given property for every category.
     */
    private static final int[] MINCOLOR = {0xFF7986CB, 0xFFF06292, 0xFFFFD54F, 0xFF4DD0E1, 0xFFAED581, 0xFFE57373, 0xFFDCE775, 0xFF80CBC4, 0xFFFFB74D};
    /**
     * Color used while displaying high level of a given property for every category.
     */
    private static final int[] MAXCOLOR = {0xFF283593, 0xFFAD1457, 0xFFFF8F00, 0xFF00838F, 0xFF558B2F, 0xFFC62828, 0xFF9E9D24, 0xFF00695C, 0xFFEF6C00};

    /**
     * Returns low level color for category.
     * @return int value of low level color for category
     */
    public int getMinColor() {
        return MINCOLOR[this.ordinal()];
    }

    /**
     * Returns high level color for category.
     * @return int value of high level color for category
     */
    public int getMaxColor() {
        return MAXCOLOR[this.ordinal()];
    }
}
