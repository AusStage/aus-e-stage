/**
 * @name MapIconMaker
 * @version 1.1
 * @author Pamela Fox
 * @copyright (c) 2008 Pamela Fox
 * @fileoverview This gives you static functions for creating dynamically
 *     sized and colored marker icons using the Charts API marker output.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('3 w={};w.1J=K(h){3 f=h.14||A;3 b=h.1f||A;3 e=h.Q||"#17";3 a=h.13||"#G";3 g=h.20||"#1W";3 d="L://z.N.S.P/z?O=1A";3 j=d+"&T="+f+"x"+b+"&V="+g.4("#","")+","+e.4("#","")+","+a.4("#","")+"&F=.E";3 c=u Z(X);c.W=j;c.10=u B(f,b);c.15=u B(y.1d(f*1.6),b);c.1a=u H(f/2,b);c.19=u H(f/2,y.1d(b/12));c.18=j+"&I=J";c.1j=j+"&C=M,s,1g"+"&I=J";j=d+"&T="+f+"x"+b+"&V="+g.4("#","")+","+e.4("#","")+","+a.4("#","");c.1l=j+"&C=a,s,1o&F=.E";c.v=[f/2,b,(7/16)*f,(5/8)*b,(5/16)*f,(7/16)*b,(7/A)*f,(5/16)*b,(5/16)*f,(1/8)*b,(1/2)*f,0,(11/16)*f,(1/8)*b,(25/A)*f,(5/16)*b,(11/16)*f,(7/16)*b,(9/16)*f,(5/8)*b];1k(3 i=0;i<c.v.1Z;i++){c.v[i]=Y(c.v[i])}D c};w.1V=K(k){3 h=k.14||A;3 j=k.1f||A;3 d=k.Q||"#17";3 i=k.1T||"#G";3 m=w.U(k.1i)||"";3 c=k.1h||"#G";3 l=k.1S||0;3 s=k.1R||"1e";3 f=(s==="1e")?"1Q":"1P";3 t="L://z.N.S.P/z?O="+f;3 n=t+"&T="+h+"x"+j+"&V="+d.4("#","")+","+i.4("#","")+"1O,1c"+"&1b="+m+"&1M="+c.4("#","")+","+l;3 e=u Z(X);e.W=n+"&C=M,s,1K"+"&F=.E";e.10=u B(h,j);e.15=u B(0,0);e.1a=u H(h/2,j/2);e.19=u H(h/2,j/2);e.18=n+"&I=J";e.1j=n+"&C=M,s,1g"+"&I=J";e.1l=n+"&C=a,s,1c&F=.E";e.v=[];R(f==="1I"){e.v=[0,0,h,0,h,j,0,j]}1H{3 o=8;3 r=1G/o;3 b=y.1F(h,j)/2;1k(3 a=0;a<(o+1);a++){3 g=r*a*(y.1E/1D);3 p=b+b*y.1B(g);3 q=b+b*y.1z(g);e.v.1L(Y(p),Y(q))}}D e};w.1y=K(k){3 j=k.Q||"#1N";3 b=k.13||"#G";3 f=k.1x||"#1w";3 a=k.1v||"#1u";3 e=w.U(k.1i)||"";3 d=k.1h||"#G";3 i=k.1t||1s;3 c=(i)?"1r":"1U";3 h="L://z.N.S.P/z?O=d&1q=1p&1b=";3 l=h+c+"\'i\\\\"+"\'["+e+"\'-2\'f\\\\"+"1X\'a\\\\]"+"h\\\\]o\\\\"+j.4("#","")+"\'1Y\\\\"+d.4("#","")+"\'1n\\\\"+b.4("#","")+"\'1m\\\\";R(i){l+=f.4("#","")+"\'1C\\\\"+a.4("#","")+"\'22\\\\"}l+="2a\'f\\\\";3 g=u Z(X);g.W=l+"&F=.E";g.10=(i)?u B(23,28):u B(21,27);D g};w.U=K(a){R(a===26){D 24}a=a.4(/@/,"@@");a=a.4(/\\\\/,"@\\\\");a=a.4(/\'/,"@\'");a=a.4(/\\[/,"@[");a=a.4(/\\]/,"@]");D 29(a)};',62,135,'|||var|replace||||||||||||||||||||||||||new|imageMap|MapIconMaker||Math|chart|32|GSize|chf|return|png|ext|000000|GPoint|chof|gif|function|http|bg|apis|cht|com|primaryColor|if|google|chs|escapeUserText_|chco|image|G_DEFAULT_ICON|parseInt|GIcon|iconSize|||strokeColor|width|shadowSize||ff0000|printImage|infoWindowAnchor|iconAnchor|chl|ffffff01|floor|circle|height|ECECD8|labelColor|label|mozPrintImage|for|transparent|eC|tC|ffffff11|mapsapi|chdp|pin_star|false|addStar|0000FF|starStrokeColor|FFFF00|starPrimaryColor|createLabeledMarkerIcon|sin|mm|cos||180|PI|min|360|else|roundrect|createMarkerIcon|00000000|push|chx|DA7187|ff|itr|it|shape|labelSize|shadowColor|pin|createFlatIcon|ffffff|hv|fC|length|cornerColor||0C||null||undefined|34|39|encodeURIComponent|Lauto'.split('|'),0,{}))
