package system.use_cases.editors;

import shared.exceptions.use_case_exceptions.InvalidInputException;
import shared.exceptions.use_case_exceptions.NoSuchAttributeException;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;


public class QuizTemplateEditor extends TemplateEditor {

    QuizTemplate template;

    public QuizTemplateEditor(QuizTemplate template) {
        this.template = new QuizTemplate(template);
    }

    @Override
    public Template getTemplate() {
        return this.template;
    }

    @Override
    public void editAttribute(String attributeName, String value)
            throws InvalidInputException, NoSuchAttributeException {
        switch (attributeName)
        {
            case "title":   // String has to match the actual instance variable name.
                editTitle(value);
                break;
            case "multipleChoice":
                editIsMultipleChoice(value);
                break;
            case "chooseAllThatApply":
                editIsChooseAllThatApply(value);
                break;
            case "hasMultipleScoreCategories":
                editHasMultipleScoreCategories(value);
                break;
            case "hasCustomEndingMessage":
                editHasCustomEndingMessage(value);
                break;
            case "hasScoreWeight":
                break;
            default:
                throw new NoSuchAttributeException();
        }
    }

    private void editHasCustomEndingMessage(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasCustomEndingMessage(Boolean.parseBoolean(value));
    }

    private void editHasMultipleScoreCategories(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setHasMultipleScoreCategories(Boolean.parseBoolean(value));
    }

    private void editIsChooseAllThatApply(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setChooseAllThatApply(Boolean.parseBoolean(value));
    }

    private void editTitle(String value) {
        template.setTitle(value);
    }

    private void editIsMultipleChoice(String value) throws InvalidInputException {
        if (!value.equals("true") && !value.equals("false"))
            throw new InvalidInputException();
        template.setMultipleChoice(Boolean.parseBoolean(value));
    }
}
