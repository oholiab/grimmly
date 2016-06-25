import weechat
w = weechat
import re
import htmllib
import os
import urllib
import urllib2

SCRIPT_NAME    = "grimmly"
SCRIPT_AUTHOR  = "oholiab <oholiab@grimmwa.re>"
SCRIPT_VERSION = "1.0"
SCRIPT_LICENSE = "MIT"
SCRIPT_DESC    = "Create short urls in private grimmly URL shortener"

settings = {
        "ignore_prefix" : "^\s+-",
        "private_server_url"    : "http://localhost:8080",
        "public_server_url"    : "https://grimmwa.re/s"
}

if w.register(SCRIPT_NAME, SCRIPT_AUTHOR, SCRIPT_VERSION, SCRIPT_LICENSE,
                    SCRIPT_DESC, "", ""):
    for option, default_value in settings.iteritems():
        if not w.config_is_set_plugin(option):
            w.config_set_plugin(option, default_value)

octet = r'(?:2(?:[0-4]\d|5[0-5])|1\d\d|\d{1,2})'
ipAddr = r'%s(?:\,.%s){3}' % (octet, octet)
# Base domain regex off RFC 1034 and 1738
label = r'[0-9a-z][-0-9a-z]*[0-9a-z]?'
domain = r'%s(?:\.%s)*\.[a-z][-0-9a-z]*[a-z]?' % (label, label)
urlRe = re.compile(r'(\w+://(?:%s|%s)(?::\d+)?(?:/[^\])>\s]*)?)' % (domain, ipAddr), re.I)
home = os.environ['HOME']
testout = open('%s/testoutput' % home, 'a')
ignoreRe = re.compile(r'(%s)' % w.config_get_plugin('ignore_prefix'))


#buffer_name = ''

#urls = {}
#script_nick = 'url'

def wee_print(message, buffer):
    weechat.prnt(buffer, '-- %s' % message)

def test_write_url(data, buffer, time, tags, displayed, highlight, prefix, message):
    #if not ignoreRe.match(message):
    #    wee_print("doing nothing", buffer)
    #    return w.WEECHAT_RC_OK
    for url in urlRe.findall(message):
        post_url = w.config_get_plugin('private_server_url')
        get_url = w.config_get_plugin('public_server_url')
        wee_print("should be %s" % get_url, buffer)
        req = urllib2.Request(post_url, url)
        response = urllib2.urlopen(req)
        shorturl = get_url + '/' + response.read()
        wee_print('%s -> %s' % (shorturl, url), buffer) 
    return w.WEECHAT_RC_OK

if __name__ == "__main__":

#        # Set default settings
        w.hook_print("", "", "://", 1, "test_write_url", "")
#        w.hook_timer(\
#            int(w.config_get_plugin('reannounce_wait')) * 1000 * 60,
#            0,
#            0,
#            "purge_cb",
#            '')
#        weechat.hook_config('plugins.var.python.%s.ignore_buffers' %SCRIPT_NAME, 'ignore_update', '')
#    color_chat_delimiters = weechat.color('chat_delimiters')
#    color_chat_nick       = weechat.color('chat_nick')
#    color_reset           = weechat.color('reset')
#    color_chat_buffer     = weechat.color('chat_buffer')
#    # pretty printing
#    script_nick = '%s[%s%s%s]%s' %(color_chat_delimiters,
#                                   color_chat_nick,
#                                   w.config_get_plugin('global_prefix'),
#                                   color_chat_delimiters,
#                                   color_reset)
