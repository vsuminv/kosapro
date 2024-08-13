document.addEventListener('DOMContentLoaded', function () {
    var section1t = document.getElementById('section1Tbody');
    var section2t = document.getElementById('section2Tbody');
    var section3t = document.getElementById('section3Tbody');
    var rowCount2 = section2t.rows.length;
    var rowCount3 = section3t.rows.length;


    if (rowCount2 > 6) {
        tableBody.style.maxHeight = '384px'; // 스크롤바가 나타날 최대 높이 설정
        tableBody.style.overflowY = 'auto'; // 세로 스크롤바 표시
    }
    if (rowCount3 > 6) {
        tableBody.style.maxHeight = '384px'; // 스크롤바가 나타날 최대 높이 설정
        tableBody.style.overflowY = 'auto'; // 세로 스크롤바 표시
    }

});
