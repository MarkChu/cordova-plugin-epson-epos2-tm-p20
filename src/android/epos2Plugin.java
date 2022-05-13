/**
 */
package com.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.util.Map; 
import java.util.HashMap;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

public class epos2Plugin extends CordovaPlugin {
    private static final String TAG = "epos2";
    private static final Map<String, Integer> printerTypeMap = new HashMap<String, Integer>() {{
        put("TM-M10", Printer.TM_M10);
        put("TM-M30", Printer.TM_M30);
        put("TM-P20", Printer.TM_P20);
        put("TM-P60", Printer.TM_P60);
        put("TM-P60II", Printer.TM_P60II);
        put("TM-P80", Printer.TM_P80);
        put("TM-T20", Printer.TM_T20);
        put("TM-T60", Printer.TM_T60);
        put("TM-T70", Printer.TM_T70);
        put("TM-T81", Printer.TM_T81);
        put("TM-T82", Printer.TM_T82);
        put("TM-T83", Printer.TM_T83);
        put("TM-T88", Printer.TM_T88);
        put("TM-T88VI", Printer.TM_T88);
        put("TM-T90", Printer.TM_T90);
        put("TM-T90KP", Printer.TM_T90KP);
        put("TM-U220", Printer.TM_U220);
        put("TM-U330", Printer.TM_U330);
        put("TM-L90", Printer.TM_L90);
        put("TM-H6000", Printer.TM_H6000);
    }};
    private static final Map<String, Integer> langTypeMap = new HashMap<String, Integer>() {{
        put("EPOS2_MODEL_ANK", Printer.MODEL_ANK);
        put("EPOS2_MODEL_CHINESE", Printer.MODEL_CHINESE);
        put("EPOS2_MODEL_TAIWAN", Printer.MODEL_TAIWAN);
        put("EPOS2_MODEL_KOREAN", Printer.MODEL_KOREAN);
        put("EPOS2_MODEL_THAI", Printer.MODEL_THAI);
        put("EPOS2_MODEL_SOUTHASIA", Printer.MODEL_SOUTHASIA);
    }};
    private static final Map<String, Integer> textLangTypeMap = new HashMap<String, Integer>() {{
        put("EPOS2_LANG_EN", Printer.LANG_EN);
        put("EPOS2_LANG_JA", Printer.LANG_JA);
        put("EPOS2_LANG_ZH_CN", Printer.LANG_ZH_CN);
        put("EPOS2_LANG_ZH_TW", Printer.LANG_ZH_TW);
        put("EPOS2_LANG_KO", Printer.LANG_KO);
        put("EPOS2_LANG_TH", Printer.LANG_TH);
        put("EPOS2_LANG_VI", Printer.LANG_VI);
        put("EPOS2_LANG_MULTI", Printer.LANG_MULTI);
        put("EPOS2_PARAM_DEFAULT", Printer.PARAM_DEFAULT);
    }};
    private static final Map<String, Integer> barcodeTypeMap = new HashMap<String, Integer>() {{
        put("EPOS2_BARCODE_UPC_A", Printer.BARCODE_UPC_A);
        put("EPOS2_BARCODE_UPC_E", Printer.BARCODE_UPC_E);
        put("EPOS2_BARCODE_EAN13", Printer.BARCODE_EAN13);
        put("EPOS2_BARCODE_JAN13", Printer.BARCODE_JAN13);
        put("EPOS2_BARCODE_EAN8", Printer.BARCODE_EAN8);
        put("EPOS2_BARCODE_JAN8", Printer.BARCODE_JAN8);
        put("EPOS2_BARCODE_CODE39", Printer.BARCODE_CODE39);
        put("EPOS2_BARCODE_ITF", Printer.BARCODE_ITF);
        put("EPOS2_BARCODE_CODABAR", Printer.BARCODE_CODABAR);
        put("EPOS2_BARCODE_CODE93", Printer.BARCODE_CODE93);
        put("EPOS2_BARCODE_CODE128", Printer.BARCODE_CODE128);
        put("EPOS2_BARCODE_GS1_128", Printer.BARCODE_GS1_128);
        put("EPOS2_BARCODE_GS1_DATABAR_OMNIDIRECTIONAL", Printer.BARCODE_GS1_DATABAR_OMNIDIRECTIONAL);
        put("EPOS2_BARCODE_GS1_DATABAR_TRUNCATED", Printer.BARCODE_GS1_DATABAR_TRUNCATED);
        put("EPOS2_BARCODE_GS1_DATABAR_LIMITED", Printer.BARCODE_GS1_DATABAR_LIMITED);
        put("EPOS2_BARCODE_GS1_DATABAR_EXPANDED", Printer.BARCODE_GS1_DATABAR_EXPANDED);
    }};
    private static final Map<String, Integer> symbolTypeMap = new HashMap<String, Integer>() {{
        put("EPOS2_SYMBOL_PDF417_STANDARD", Printer.SYMBOL_PDF417_STANDARD);
        put("EPOS2_SYMBOL_PDF417_TRUNCATED", Printer.SYMBOL_PDF417_TRUNCATED);
        put("EPOS2_SYMBOL_QRCODE_MODEL_1", Printer.SYMBOL_QRCODE_MODEL_1);
        put("EPOS2_SYMBOL_QRCODE_MODEL_2", Printer.SYMBOL_QRCODE_MODEL_2);
        put("EPOS2_SYMBOL_QRCODE_MICRO", Printer.SYMBOL_QRCODE_MICRO);
        put("EPOS2_SYMBOL_MAXICODE_MODE_2", Printer.SYMBOL_MAXICODE_MODE_2);
        put("EPOS2_SYMBOL_MAXICODE_MODE_3", Printer.SYMBOL_MAXICODE_MODE_3);
        put("EPOS2_SYMBOL_MAXICODE_MODE_5", Printer.SYMBOL_MAXICODE_MODE_5);
        put("EPOS2_SYMBOL_MAXICODE_MODE_6", Printer.SYMBOL_MAXICODE_MODE_6);
        put("EPOS2_SYMBOL_GS1_DATABAR_STACKED", Printer.SYMBOL_GS1_DATABAR_STACKED);
        put("EPOS2_SYMBOL_GS1_DATABAR_STACKED_OMNIDIRECTIONAL", Printer.SYMBOL_GS1_DATABAR_STACKED_OMNIDIRECTIONAL);
        put("EPOS2_SYMBOL_GS1_DATABAR_EXPANDED_STACKED", Printer.SYMBOL_GS1_DATABAR_EXPANDED_STACKED);
        put("EPOS2_SYMBOL_AZTECCODE_FULLRANGE", Printer.SYMBOL_AZTECCODE_FULLRANGE);
        put("EPOS2_SYMBOL_AZTECCODE_COMPACT", Printer.SYMBOL_AZTECCODE_COMPACT);
        put("EPOS2_SYMBOL_DATAMATRIX_SQUARE", Printer.SYMBOL_DATAMATRIX_SQUARE);
        put("EPOS2_SYMBOL_DATAMATRIX_RECTANGLE_8", Printer.SYMBOL_DATAMATRIX_RECTANGLE_8);
        put("EPOS2_SYMBOL_DATAMATRIX_RECTANGLE_12", Printer.SYMBOL_DATAMATRIX_RECTANGLE_12);
        put("EPOS2_SYMBOL_DATAMATRIX_RECTANGLE_16", Printer.SYMBOL_DATAMATRIX_RECTANGLE_16);
    }};
    private static final Map<String, Integer> levelMap = new HashMap<String, Integer>() {{
        put("EPOS2_LEVEL_0", Printer.LEVEL_0);
        put("EPOS2_LEVEL_1", Printer.LEVEL_1);
        put("EPOS2_LEVEL_2", Printer.LEVEL_2);
        put("EPOS2_LEVEL_3", Printer.LEVEL_3);
        put("EPOS2_LEVEL_4", Printer.LEVEL_4);
        put("EPOS2_LEVEL_5", Printer.LEVEL_5);
        put("EPOS2_LEVEL_6", Printer.LEVEL_6);
        put("EPOS2_LEVEL_7", Printer.LEVEL_7);
        put("EPOS2_LEVEL_8", Printer.LEVEL_8);
        put("EPOS2_PARAM_DEFAULT", Printer.PARAM_DEFAULT);
        put("EPOS2_LEVEL_L", Printer.LEVEL_L);
        put("EPOS2_LEVEL_M", Printer.LEVEL_M);
        put("EPOS2_LEVEL_Q", Printer.LEVEL_Q);
        put("EPOS2_LEVEL_H", Printer.LEVEL_H);
    }};
    private CallbackContext discoverCallbackContext = null;
    private CallbackContext sendDataCallbackContext = null;
    private Printer printer = null;
    private String printerTarget = null;
    private int printerSeries = Printer.TM_P20;
    private boolean printerConnected = false;
    // use for other language
    private int printerLang = Printer.MODEL_TAIWAN;
    private int textLanguage = Printer.LANG_ZH_TW;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                if (action.equals("startDiscover")) {
                    startDiscovery(callbackContext);
                } else if (action.equals("stopDiscover")) {
                    stopDiscovery(callbackContext);
                } else if (action.equals("connectPrinter")) {
                    connectPrinter(args, callbackContext);
                } else if (action.equals("disconnectPrinter")) {
                    disconnectPrinter(args, callbackContext);
                } else if (action.equals("printText")) {
                    printText(args, callbackContext);
                } else if (action.equals("printBarCode")) {
                    printBarCode(args, callbackContext);
                } else if (action.equals("printSymbol")) {
                    printSymbol(args, callbackContext);    
                } else if (action.equals("printLine")) {
                    printLine(args, callbackContext);    
                } else if (action.equals("printImage")) {
                    printImage(args, callbackContext);
                } else if (action.equals("sendData")) {
                    sendData(args, callbackContext);
                } else if (action.equals("getPrinterStatus")) {
                    getPrinterStatus(args, callbackContext);
                } else if (action.equals("getSupportedModels")) {
                    getSupportedModels(args, callbackContext);
                }
            }
        });

        return true;
    }

    private void startDiscovery(final CallbackContext callbackContext) {
        // discovery is still running, try to stop it first
        if (discoverCallbackContext != null) {
            try {
                Discovery.stop();
            } catch (Epos2Exception e) {
                Log.e(TAG, "Failed to stop running discovery", e);
            }
        }

        Log.d(TAG, "Start discovery");
        discoverCallbackContext = callbackContext;

        FilterOption mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        mFilterOption.setBondedDevices(Discovery.TRUE);
        try {
            Discovery.start(webView.getContext(), mFilterOption, discoveryListener);
        } catch (Epos2Exception e) {
            Log.e(TAG, "Error 0x00001: Printer discovery failed: " + e.getErrorStatus(), e);
            callbackContext.error("Error discovering printer: " + e.getErrorStatus());
        }
    }

    private void stopDiscovery(final CallbackContext callbackContext) {
        Log.d(TAG, "Stop discovery");

        while (true) {
            try {
                Discovery.stop();
                PluginResult result = new PluginResult(Status.OK, true);
                callbackContext.sendPluginResult(result);
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    PluginResult result = new PluginResult(Status.ERROR, false);
                    callbackContext.sendPluginResult(result);
                    break;
                }
            }
        }

        discoverCallbackContext = null;
    }

    private void connectPrinter(final JSONArray args, final CallbackContext callbackContext) {
        String target;

        try {
            target = args.getString(0);
            if (args.length() > 1) {
                int typeEnum = printerTypeFromString(args.getString(1));
                if (typeEnum >= 0) {
                    printerSeries = typeEnum;
                }
            }
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Error connecting printer", e);
            return;
        }

        // check for existing connection
        if (printer != null && printerConnected && !printerTarget.equals(target)) {
            callbackContext.error("Error 0x00011: Printer already connected");
            Log.w(TAG, "Printer already connected");
            return;
        }

        printerTarget = target;

        if (_connectPrinter(callbackContext)) {
            PluginResult result = new PluginResult(Status.OK, true);
            callbackContext.sendPluginResult(result);
        }
    }

    private boolean _connectPrinter(final CallbackContext callbackContext) {
        if (printerConnected) {
            return true;
        }

        Log.d(TAG, "_connectPrinter() to " + printerTarget);

        try {
            printer = new Printer(printerSeries, printerLang, webView.getContext());
            printer.setReceiveEventListener(receiveListener);
        }
        catch (Epos2Exception e) {
            callbackContext.error("Error 0x00012: Creating printer failed: " + e.getErrorStatus());
            Log.e(TAG, "Error creating printer: " + e.getErrorStatus(), e);
            return false;
        }

        try {
            printer.connect(printerTarget, Printer.PARAM_DEFAULT);
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00012: Connecting printer failed: " + e.getErrorStatus());
            Log.e(TAG, "Error connecting printer: " + e.getErrorStatus(), e);
            return false;
        }

        try {
            printer.beginTransaction();
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00012: Beginning transaction failed");
            Log.e(TAG, "Error beginning transaction", e);
            return false;
        }

        printerConnected = true;

        return true;
    }

    private void disconnectPrinter(final JSONArray args, final CallbackContext callbackContext) {
        if (printer == null) {
            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
            return;
        }

        try {
            printer.endTransaction();
        }
        catch (Epos2Exception e) {
            callbackContext.error("Error 0x00020: Ending transaction failed: " + e.getErrorStatus());
            Log.e(TAG, "Error ending transaction: " + e.getErrorStatus(), e);
            return;
        }

        try {
            printer.disconnect();
        }
        catch (Epos2Exception e) {
            callbackContext.error("Error 0x00021: Disconnecting printer failed: " + e.getErrorStatus());
            Log.e(TAG, "Error disconnecting printer: " + e.getErrorStatus(), e);
            return;
        }

        printer.clearCommandBuffer();
        printer.setReceiveEventListener(null);
        printerConnected = false;
        printer = null;

        PluginResult result = new PluginResult(Status.OK, true);
        callbackContext.sendPluginResult(result);
    }

    private void printText(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        JSONArray printData;
        int textFont = Printer.PARAM_DEFAULT;
        int textSize = Printer.PARAM_DEFAULT;
        int textAlign = Printer.PARAM_DEFAULT;

        try {
            printData = args.getJSONArray(0);

            if (args.length() > 1) {
                textFont = args.getInt(1);
            }
            if (args.length() > 2) {
                textSize = args.getInt(2);
            }
            if (args.length() > 3) {
                textAlign = args.getInt(3);
            }
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Invalid arguments for printText", e);
            return;
        }

        try {
            printer.addTextLang(textLanguage);
            printer.addTextFont(textFont);
            printer.addTextSize(textSize, textSize);
            printer.addTextAlign(textAlign);

            for (int i = 0; i < printData.length(); i++) {
                String data = printData.getString(i);
                if ("\n".equals(data)) {
                    printer.addFeedLine(1);
                } else {
                    printer.addText(data);
                }
            }

            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00030: Failed to add text data");
            Log.e(TAG, "Error printing", e);
            try {
                printer.disconnect();
                printerConnected = false;
            }
            catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Failed to read input data: " + e.getCause());
            Log.e(TAG, "Error getting data", e);
        }
    }

    private void printBarCode(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        String data = "";
        int bType = Printer.BARCODE_CODE128;
        int hriPosition = 0;
        int hriFont = 0;
        int bWidth = 2;
        int bHeight = 70;

        try {
            data = args.getString(0);

            if (args.length() > 1) {
                String type = args.getString(1);
                bType = barcodeTypeMap.get(type);
            }
            if (args.length() > 2) {
                hriPosition = args.getInt(2);
            }
            if (args.length() > 3) {
                hriFont = args.getInt(3);
            }
            if (args.length() > 4) {
                bWidth = args.getInt(4);
            }
            if (args.length() > 5) {
                bHeight = args.getInt(5);
            }                        
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Invalid arguments for printBarCode", e);
            return;
        }

        try {
            printer.addBarcode(data, bType, hriPosition, hriFont, bWidth, bHeight);

            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00040: Failed to add barcode data");
            Log.e(TAG, "Error printing", e);
            try {
                printer.disconnect();
                printerConnected = false;
            } catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        }
    }

    private void printSymbol(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        String data = "";
        int sType = Printer.SYMBOL_QRCODE_MODEL_2;
        int level = Printer.PARAM_DEFAULT;
        int width = 3;
        int height = 3;
        int size = 0;

        try {
            data = args.getString(0);

            if (args.length() > 1) {
                String type = args.getString(1);
                sType = symbolTypeMap.get(type);
            }
            if (args.length() > 2) {
                String levelName = args.getString(2);
                level = levelMap.get(levelName);
            }
            if (args.length() > 3) {
                width = args.getInt(3);
            }
            if (args.length() > 4) {
                height = args.getInt(4);
            }
            if (args.length() > 5) {
                size = args.getInt(5);
            }                        
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Invalid arguments for printBarCode", e);
            return;
        }

        try {
            printer.addSymbol(data, sType, level, width, height, size);

            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00040: Failed to add symbol data");
            Log.e(TAG, "Error printing", e);
            try {
                printer.disconnect();
                printerConnected = false;
            } catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        }
    }

    private void printLine(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        int startX = 0;
        int endX = 100;
        int lineStyle = Printer.LINE_THIN;

        try {
            startX = args.getInt(0);

            if (args.length() > 1) {
                endX = args.getInt(1);
            }
            if (args.length() > 2) {
                lineStyle = args.getInt(2);
            }
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Invalid arguments for printLine", e);
            return;
        }

        if (endX > 382) {
            callbackContext.error("Error 0x00000: endX max value is 382. now:" + Integer.toString(endX));
            Log.e(TAG, "Invalid arguments for printLine.");
            return;
        }

        try {
            // use addPageline command
            printer.addPageBegin();
            printer.addPageArea(0, 0, 388, 6);
            printer.addPageDirection(Printer.DIRECTION_LEFT_TO_RIGHT);
            printer.addPagePosition(0, 0);
            printer.addPageLine(startX, 0, endX, 0, lineStyle);
            printer.addPageEnd();

            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00040: Failed to add line data");
            Log.e(TAG, "Error printing", e);
            try {
                printer.disconnect();
                printerConnected = false;
            } catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        }
    }


    private void printImage(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        String imageDataUrl;
        int printMode = Printer.MODE_MONO;
        int halfTone = Printer.HALFTONE_THRESHOLD;

        try {
            imageDataUrl = args.getString(0);

            if (args.length() > 1) {
                printMode = args.getInt(1);
            }
            if (args.length() > 2) {
                halfTone = args.getInt(2);
            }
        } catch (JSONException e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Invalid arguments for printImage", e);
            return;
        }

        try {
            // create Bitmap image from data-url
            final String imageData = imageDataUrl.substring(imageDataUrl.indexOf(",") + 1);

            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (image == null) {
                throw new IllegalArgumentException("Empty result from BitmapFactory.decodeByteArray()");
            }

            Log.d(TAG, String.format("addImage with data: %dx%d pixels", image.getWidth(), image.getHeight()));

            printer.addImage(image, 0, 0, image.getWidth(), image.getHeight(), Printer.COLOR_1, printMode, halfTone, Printer.PARAM_DEFAULT, Printer.COMPRESS_AUTO);

            callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
        } catch (IllegalArgumentException e) {
            callbackContext.error("Error 0x00040: Failed to convert image data");
            Log.e(TAG, "Invalid image data", e);
        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00040: Failed to add image data");
            Log.e(TAG, "Error printing", e);
            try {
                printer.disconnect();
                printerConnected = false;
            } catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        }
    }

    private void sendData(final JSONArray args, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("Error 0x00013: Printer is not connected");
            return;
        }

        sendDataCallbackContext = callbackContext;

        // check printer status (cached)
        PrinterStatusInfo status = printer.getStatus();

        if (!isPrintable(status)) {
            callbackContext.error("Error 0x00050: Printer is not ready. Check device and paper.");
            Log.e(TAG, "Error printing: printer is not printable");

            try {
                printer.disconnect();
                printerConnected = false;
            }
            catch (Epos2Exception ex) {
                callbackContext.error("Error disconnecting");
                Log.e(TAG, "Error disconnecting", ex);
            }
            return;
        }

        try {
            printer.addFeedLine(3);
            printer.addCut(Printer.CUT_FEED);

            printer.sendData(Printer.PARAM_DEFAULT);

            printer.clearCommandBuffer();

        } catch (Epos2Exception e) {
            callbackContext.error("Error 0x00051: Failed to send print job");
            Log.e(TAG, "Error in sendData()", e);

            try {
                printer.clearCommandBuffer();
                printer.disconnect();
                printerConnected = false;
            } catch (Epos2Exception ex) {
                Log.e(TAG, "Error disconnecting", ex);
            }
        }
    }

    private void getPrinterStatus(final JSONArray array, final CallbackContext callbackContext) {
        if (!_connectPrinter(callbackContext)) {
            callbackContext.error("printer not found");
            return;
        }

        try {
            PrinterStatusInfo status = printer.getStatus();

            JSONObject info = new JSONObject();
            info.put("online", status.getOnline());
            info.put("connection", status.getConnection());
            info.put("coverOpen", status.getCoverOpen());
            info.put("paper", status.getPaper());
            info.put("paperFeed", status.getPaperFeed());
            info.put("errorStatus", status.getErrorStatus());
            info.put("isPrintable", isPrintable(status));

            callbackContext.success(info);
        } catch (JSONException e) {
            callbackContext.error("Error building device status result");
        }
    }

    private void getSupportedModels(final JSONArray array, final CallbackContext callbackContext) {
        JSONArray types = new JSONArray(printerTypeMap.keySet());
        callbackContext.success(types);
    }

    private DiscoveryListener discoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            Log.d(TAG, "DiscoveryListener.onDiscovery: " + deviceInfo.getTarget());
            JSONObject item = new JSONObject();

            try {
                item.put("deviceName", deviceInfo.getDeviceName());
                item.put("target", deviceInfo.getTarget());
                item.put("ipAddress", deviceInfo.getIpAddress());
                item.put("macAddress", deviceInfo.getMacAddress());
                item.put("deviceType", deviceInfo.getDeviceType());
                item.put("bdAddress", deviceInfo.getBdAddress());
            } catch (JSONException e) {
                discoverCallbackContext.error("Error building device info");
            }

            PluginResult pluginResult = new PluginResult(Status.OK, item);
            pluginResult.setKeepCallback(true); // keep the callback open to send the info of more than one device to the application
            discoverCallbackContext.sendPluginResult(pluginResult);
        }
    };

    private ReceiveListener receiveListener = new ReceiveListener() {
        @Override
        public void onPtrReceive(final Printer printer, final int code, final PrinterStatusInfo status, final String printJobId) {
            Log.d(TAG, String.format("onPtrReceive; code : %d, status: %d, printJobId: %s", code, status.getErrorStatus(), printJobId));

            // send callback for sendData command
            if (sendDataCallbackContext != null) {
                if (code == Epos2CallbackCode.CODE_SUCCESS) {
                    sendDataCallbackContext.sendPluginResult(new PluginResult(Status.OK, true));
                } else {
                    sendDataCallbackContext.error("Error 0x00050: Print job failed. Check the device.");
                }
                sendDataCallbackContext = null;
            }
        }
    };

    private int printerTypeFromString(String type) {
        if (printerTypeMap.containsKey(type)) {
            return printerTypeMap.get(type).intValue();
        }

        return -1;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }

        return true;
    }

    private void setLang(final JSONArray args, final CallbackContext callbackContext) {
        try {
            printerLang = langTypeMap.get(args.getString(0)).intValue();
            if (args.length() > 1) {
                textLanguage = textLangTypeMap.get(args.getString(1)).intValue();
            }
        } catch (Exception e) {
            callbackContext.error("Error 0x00000: Invalid arguments: " + e.getCause());
            Log.e(TAG, "Error setting language", e);
            return;
        }
    }
}
