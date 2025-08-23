package com.S1_K4.ForkMe_BE.modules.chatbot.setting;
import java.util.Set;
import java.util.regex.Pattern;
/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.dto
 * @fileName : InputValidator
 * @date : 2025-08-20
 * @description : 챗봇 입력 검증 유틸
 */
public final class InputValidator {

    private InputValidator() {}

    // 공통: 공백/너무 짧음/반복 문자/숫자만/기호만 등
    private static final Pattern REPEAT_SAME_CHAR = Pattern.compile("^([\\W\\w])\\1{2,}$"); // 같은 문자 3회 이상
    private static final Pattern ONLY_DIGITS = Pattern.compile("^\\d+$");
    private static final Pattern ONLY_PUNCT = Pattern.compile("^[\\p{Punct}\\s]+$");

    // 스택 검증: 한글/영문/숫자/+,#,-,.,공백,콤마 허용. 최소 2개 토큰 권장.
    private static final Pattern STACK_ALLOWED = Pattern.compile("^[a-zA-Z0-9가-힣 ,.+#\\-_/]{3,100}$");

    // 기간 파싱: 1~24개월, 1~52주, 1~180일 정도 허용
    private static final Pattern MONTHS = Pattern.compile("^(\\d{1,2})\\s*(개월|달|month|months|mo)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEEKS  = Pattern.compile("^(\\d{1,2})\\s*(주|weeks?|w)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DAYS   = Pattern.compile("^(\\d{1,3})\\s*(일|days?|d)$", Pattern.CASE_INSENSITIVE);

    // 멤버 수: 1~20명
    private static final Pattern MEMBERS = Pattern.compile("^(\\d{1,2})\\s*(명)?$");

    private static final Set<String> YES_SET = Set.of("네","yes","y","예","ㅇㅇ","ㅇ");
    private static final Set<String> NO_SET  = Set.of("아니요","아니오","no","n","ㄴㄴ","ㄴ");

    public static boolean isYes(String s) {
        if (s == null) return false;
        return YES_SET.contains(s.trim().toLowerCase());
    }
    public static boolean isNo(String s) {
        if (s == null) return false;
        return NO_SET.contains(s.trim().toLowerCase());
    }

    public static boolean looksMeaningless(String s) {
        if (s == null) return true;
        String t = s.trim();
        if (t.length() < 2) return true;
        if (REPEAT_SAME_CHAR.matcher(t).matches()) return true;
        if (ONLY_DIGITS.matcher(t).matches()) return true;
        if (ONLY_PUNCT.matcher(t).matches()) return true;
        return false;
    }

    /** 스택 문자열 검증(쉼표 구분 권장) */
    public static boolean validateStack(String stack) {
        if (looksMeaningless(stack)) return false;
        if (!STACK_ALLOWED.matcher(stack).matches()) return false;
        // 최소 1~2개 키워드 포함
        String[] parts = stack.split("[,/ ]+");
        int tokenLike = 0;
        for (String p : parts) if (p.length() >= 2) tokenLike++;
        return tokenLike >= 1; // 2 이상으로 올리고 싶으면 2로 변경
    }

    /** 기간 문자열을 정규화. 유효하면 "X개월"/"X주"/"X일" 반환, 아니면 null */
    public static String normalizeDuration(String duration) {
        if (looksMeaningless(duration)) return null;
        var t = duration.trim().toLowerCase();

        var m = MONTHS.matcher(t);
        if (m.matches()) {
            int n = Integer.parseInt(m.group(1));
            if (n >= 1 && n <= 24) return n + "개월";
        }
        var w = WEEKS.matcher(t);
        if (w.matches()) {
            int n = Integer.parseInt(w.group(1));
            if (n >= 1 && n <= 52) return n + "주";
        }
        var d = DAYS.matcher(t);
        if (d.matches()) {
            int n = Integer.parseInt(d.group(1));
            if (n >= 1 && n <= 180) return n + "일";
        }
        // 한국어 텍스트 입력 대비 간단 처리
        if (t.contains("개월") || t.contains("달")) return t;
        if (t.contains("주") || t.contains("일")) return t;

        return null;
    }

    /** 멤버 수 정규화. 유효하면 "N명" 리턴, 아니면 null */
    public static String normalizeMembers(String members) {
        if (looksMeaningless(members)) return null;
        var m = MEMBERS.matcher(members.trim().toLowerCase());
        if (!m.matches()) return null;
        int n = Integer.parseInt(m.group(1));
        if (n < 1 || n > 20) return null;
        return n + "명";
    }
}