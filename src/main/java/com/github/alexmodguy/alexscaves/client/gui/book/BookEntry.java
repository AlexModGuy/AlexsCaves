package com.github.alexmodguy.alexscaves.client.gui.book;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.book.widget.BookWidget;
import com.github.alexmodguy.alexscaves.server.misc.CaveBookProgress;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookEntry {

    public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BookEntry.class, new BookEntry.Deserializer()).excludeFieldsWithoutExposeAnnotation().create();
    private static Pattern pattern = Pattern.compile("\\{.*?\\}");
    @Expose
    private String translatableTitle;
    @Expose
    private String parent;
    @Expose
    private String textFileToReadFrom;
    @Expose
    private String requiredProgress;
    @Expose
    private BookWidget[] widgets;
    private List<String> entryText = new ArrayList<>();
    private List<BookLink> bookLinks = new ArrayList<>();

    private int pageCount = 0;


    public BookEntry(String translatableTitle, String parent, String textFileToReadFrom, String requiredProgress, BookWidget[] widgets) {
        this.translatableTitle = translatableTitle;
        this.parent = parent;
        this.textFileToReadFrom = textFileToReadFrom;
        this.requiredProgress = requiredProgress;
        this.widgets = widgets;

    }

    public static BookEntry deserialize(Reader readerIn) {
        return GsonHelper.fromJson(GSON, readerIn, BookEntry.class);
    }

    public String getTranslatableTitle() {
        return translatableTitle;
    }

    public String getParent() {
        return parent;
    }

    public List<String> getEntryText() {
        return entryText;
    }

    public List<BookLink> getEntryLinks() {
        return bookLinks;
    }

    public BookWidget[] getWidgets() {
        return widgets;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void init(CaveBookScreen screen) {
        this.entryText = getRawTextFromFile(textFileToReadFrom, screen, 30);
        this.pageCount = (int) Math.ceil(entryText.size() / (float) (CaveBookScreen.PAGE_SIZE_IN_LINES * 2));
    }

    private List<String> getRawTextFromFile(String fileName, CaveBookScreen screen, int maxLineSize) {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
        ResourceLocation fileRes;
        try {
            fileRes = ResourceLocation.parse(CaveBookScreen.getBookFileDirectory() + lang + "/" + fileName);
            //test if it exists. if no exception, then the language is supported
            InputStream is = Minecraft.getInstance().getResourceManager().open(fileRes);
            is.close();
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Could not find language file for translation, defaulting to en_us");
            fileRes = ResourceLocation.parse(CaveBookScreen.getBookFileDirectory() + "en_us/" + fileName);
        }
        List<String> strings = new ArrayList<>();
        Font font = Minecraft.getInstance().font;
        try {
            BufferedReader bufferedreader = Minecraft.getInstance().getResourceManager().openAsReader(fileRes);
            List<String> readIn = IOUtils.readLines(bufferedreader);
            int currentLineCount = 0;
            for (String readString : readIn) {
                Matcher m = pattern.matcher(readString);
                boolean skipLineEntirely = false;
                boolean noOverflow = false;
                while (m.find()) {
                    String[] found = m.group().split("\\|");
                    if (found.length >= 1) {
                        String linkTo = found[1].substring(0, found[1].length() - 1);
                        int visiblity = screen.getEntryVisiblity(linkTo);
                        String display = "";
                        if(visiblity != 2){
                            display = visiblity == 0 ? found[0].substring(1) : "???";
                            bookLinks.add(new BookLink(currentLineCount, m.start(), display, linkTo, visiblity == 0));
                            readString = m.replaceFirst(display);
                        }else{
                            readString = display;
                            skipLineEntirely = true;
                        }
                        noOverflow = true;
                    }
                }
                if(readString.isEmpty() && !skipLineEntirely){
                    strings.add(readString);
                    currentLineCount++;
                }
                while (font.width(readString) > maxLineSize) {
                    int spaceScanIndex = 0;
                    int lastSpace = -1;
                    while(spaceScanIndex < readString.length()){
                        if(readString.charAt(spaceScanIndex) == ' ' && font.width(readString.substring(0, spaceScanIndex)) > 92){
                            lastSpace = noOverflow ? readString.length() : spaceScanIndex;
                            break;
                        }
                        spaceScanIndex++;
                    }
                    int cutIndex = lastSpace == -1 ? Math.min(maxLineSize, readString.length()) : lastSpace;
                    strings.add(readString.substring(0, cutIndex));
                    currentLineCount++;
                    readString = readString.substring(cutIndex);
                    if (readString.startsWith(" ")) {
                        readString = readString.substring(1);
                    }
                }
                if(!readString.isEmpty()){
                    strings.add(readString);
                    currentLineCount++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    public void mouseOver(CaveBookScreen screen, int page, float mouseX, float mouseY){
        boolean hoverFlag = false;
        screen.unlockTooltip = false;
        for(BookLink link : bookLinks){
            int minLine = page * CaveBookScreen.PAGE_SIZE_IN_LINES;
            link.setHovered(false);
            if(link.getLineNumber() >= minLine && link.getLineNumber() <= minLine + CaveBookScreen.PAGE_SIZE_IN_LINES * 2){
                String line = entryText.get(link.getLineNumber());
                boolean rightPage = link.getLineNumber() > minLine + CaveBookScreen.PAGE_SIZE_IN_LINES;
                float textStartsX = rightPage ? 0.03F : -0.71F;
                float textsStartsY = -0.38F;
                float wordStartAt = textStartsX + Minecraft.getInstance().font.width(line.substring(0, link.getCharacterStartsAt())) * 0.00475F;
                float wordEndAt = wordStartAt + Minecraft.getInstance().font.width(link.getDisplayText()) * 0.005F;
                float wordTopAt = textsStartsY + (link.getLineNumber() % CaveBookScreen.PAGE_SIZE_IN_LINES) * 0.0425F;
                float wordBottomAt = wordTopAt + 0.05F;
                if(mouseX > wordStartAt && mouseX < wordEndAt && mouseY > wordTopAt && mouseY < wordBottomAt){
                    if(link.isEnabled()){
                        link.setHovered(!hoverFlag);
                        hoverFlag = true;
                    }else{
                        screen.unlockTooltip = true;
                    }
                }
            }
        }
    }

    public boolean consumeMouseClick(CaveBookScreen screen){
        for(BookLink link : bookLinks) {
            int minLine = screen.getEntryPageNumber() * CaveBookScreen.PAGE_SIZE_IN_LINES;
            if(link.isEnabled() && link.isHovered() && link.getLineNumber() >= minLine && link.getLineNumber() <= minLine + CaveBookScreen.PAGE_SIZE_IN_LINES * 2) {
                return screen.attemptChangePage(ResourceLocation.parse(CaveBookScreen.getBookFileDirectory() + link.getLinksTo()), true);
            }
        }
        return false;
    }

    /*
        0 = always visible
        1 = ??? (no link)
        2 = super secret, no ???
     */
    public int getVisibility(CaveBookScreen caveBookScreen){
        if(this.requiredProgress == null){
            return 0;
        }else{
            if(caveBookScreen.getCaveBookProgress().isUnlockedFor(requiredProgress)){
                return 0;
            }else {
                CaveBookProgress.Subcategory subcategory = caveBookScreen.getCaveBookProgress().getSubcategoryFromPage(requiredProgress);
                return subcategory == CaveBookProgress.Subcategory.SECRETS ? 2 : 1;
            }
        }
    }

    public static class Deserializer implements JsonDeserializer<BookEntry> {

        public BookEntry deserialize(JsonElement mainElement, Type deserializeType, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(mainElement, "book entry");
            BookWidget[] bookWidgets = new BookWidget[0];
            if(jsonobject.has("widgets")){
                JsonArray jsonArray = jsonobject.getAsJsonArray("widgets");
                bookWidgets = new BookWidget[jsonArray.size()];
                for(int i = 0; i < jsonArray.size(); i++){
                    JsonObject widgetJson = jsonArray.get(i).getAsJsonObject();
                    BookWidget.Type type = GsonHelper.getAsObject(widgetJson, "type", context, BookWidget.Type.class);
                    bookWidgets[i] = GsonHelper.convertToObject(widgetJson, "", context, type.getWidgetClass());
                }
            }
            String parent = null;
            if (jsonobject.has("parent")) {
                parent = GsonHelper.getAsString(jsonobject, "parent");
            }

            String text = "";
            if (jsonobject.has("text")) {
                text = GsonHelper.getAsString(jsonobject, "text");
            }

            String title = "";
            if (jsonobject.has("title")) {
                title = GsonHelper.getAsString(jsonobject, "title");
            }

            String progress = null;
            if (jsonobject.has("required_progression")) {
                progress = GsonHelper.getAsString(jsonobject, "required_progression");
            }

            BookEntry bookEntry = new BookEntry(title, parent, text, progress, bookWidgets);
            return bookEntry;
        }
    }
}
