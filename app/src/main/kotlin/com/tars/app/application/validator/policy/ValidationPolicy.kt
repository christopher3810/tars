package com.tars.app.application.validator.policy

interface ValidationPolicy {

    //TODO : Type 객체 대신 Map 사용중인데 data class 를 가져와서 밀어넣기도 매번 패키지 경로 임포트 뜨고 쫌 짜침.
    // 이정도는 Map 사용으론 무난해보임 고민은 해보자.
    fun validate(input: Map<String, Any?>)
}