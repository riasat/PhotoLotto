package com.abir.photolotto;

import java.util.ArrayList;
import java.util.List;

public class EventModel {

	private String  sEventType = "";
	private String	sTwitterMsg = "";
	private String	sFacebookUrl = "";
	private String	sHtmlBefore = "";
	private String	sName = "";
	private String	sImgThumb = "";
	private String	sImageOverlay1 = "";
	private String	sImageOverlay2 = "";
	private String	sImageOverlay3 = "";
	private String	sImageOverlay4 = "";
	private String	sImageOverlay5 = "";
	private String	sCompanyName = "";
	private String	fDistance = "";
	private String	sShortDescription1 = "";
	private String	sShortDescription2 = "";
	private String	sFacebookMessage = "";
	private String	sHtmlAfter = "";
	private String  sTerms = "";
	private int		nNumberOfOverlay;
	private long	lId;
	private static 	EventModel event;
	public static List<EventModel> lstNearEvents = new ArrayList<EventModel>();
	public static List<EventModel> lstNationalEvents = new ArrayList<EventModel>();
	public static List<EventModel> lstPixtaEvents = new ArrayList<EventModel>();
	
	public static EventModel getSelectedEvent(){
		return event;
	}
	
	public static void setEvent(EventModel em){
		event = em;
	}
	
	public EventModel() {
	}

	public String getsTwitterMsg() {
		return sTwitterMsg;
	}

	public void setsTwitterMsg(String sTwitterMsg) {
		this.sTwitterMsg = sTwitterMsg;
	}

	public String getsFacebookUrl() {
		return sFacebookUrl;
	}

	public void setsFacebookUrl(String sFacebookUrl) {
		this.sFacebookUrl = sFacebookUrl;
	}

	public String getsHtmlBefore() {
		return sHtmlBefore;
	}

	public void setsHtmlBefore(String sHtmlBefore) {
		this.sHtmlBefore = sHtmlBefore;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getsImgThumb() {
		return sImgThumb;
	}

	public void setsImgThumb(String sImgThumb) {
		this.sImgThumb = sImgThumb;
	}

	public String getsImageOverlay1() {
		return sImageOverlay1;
	}

	public void setsImageOverlay1(String sImageOverlay1) {
		this.sImageOverlay1 = sImageOverlay1;
	}

	public String getsImageOverlay2() {
		return sImageOverlay2;
	}

	public void setsImageOverlay2(String sImageOverlay2) {
		this.sImageOverlay2 = sImageOverlay2;
	}

	public String getsImageOverlay3() {
		return sImageOverlay3;
	}

	public void setsImageOverlay3(String sImageOverlay3) {
		this.sImageOverlay3 = sImageOverlay3;
	}

	public String getsImageOverlay4() {
		return sImageOverlay4;
	}

	public void setsImageOverlay4(String sImageOverlay4) {
		this.sImageOverlay4 = sImageOverlay4;
	}

	public String getsImageOverlay5() {
		return sImageOverlay5;
	}

	public void setsImageOverlay5(String sImageOverlay5) {
		this.sImageOverlay5 = sImageOverlay5;
	}

	public String getsCompanyName() {
		return sCompanyName;
	}

	public void setsCompanyName(String sCompanyName) {
		this.sCompanyName = sCompanyName;
	}

	public String getfDistance() {
		return fDistance;
	}

	public void setfDistance(String fDistance) {
		this.fDistance = fDistance;
	}

	public long getlId() {
		return lId;
	}

	public void setlId(long lId) {
		this.lId = lId;
	}

	public String getsShortDescription1() {
		return sShortDescription1;
	}

	public void setsShortDescription1(String sShortDescription) {
		this.sShortDescription1 = sShortDescription;
	}

	public String getsShortDescription2() {
		return sShortDescription2;
	}

	public void setsShortDescription2(String sShortDescription) {
		this.sShortDescription2 = sShortDescription;
	}

	
	public String getsFacebookMessage() {
		return sFacebookMessage;
	}

	public void setsFacebookMessage(String sFacebookMessage) {
		this.sFacebookMessage = sFacebookMessage;
	}

	public int getnNumberOfOverlay() {
		return nNumberOfOverlay;
	}

	public void setnNumberOfOverlay(int nNumberOfOverlay) {
		this.nNumberOfOverlay = nNumberOfOverlay;
	}

	public String getsHtmlAfter() {
		return sHtmlAfter;
	}

	public void setsHtmlAfter(String sHtmlAfter) {
		this.sHtmlAfter = sHtmlAfter;
	}

	public String getsTerms() {
		return sTerms;
	}

	public void setsTerms(String sTerms) {
		this.sTerms = sTerms;
	}

	public String getsEventType() {
		return sEventType;
	}

	public void setsEventType(String sEventType) {
		this.sEventType = sEventType;
	}
	
	@Override
	public String toString() {
		return getsName();
	}
}
